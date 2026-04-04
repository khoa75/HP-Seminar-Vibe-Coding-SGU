package vn.tt.practice.apigateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import vn.tt.practice.apigateway.util.JwtUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * JwtAuthenticationFilter — GlobalFilter thực thi JWT authentication tại Gateway.
 *
 * Không dùng WebSecurityConfigurerAdapter (đã bị remove trong Spring Security 6).
 * Toàn bộ xử lý là reactive (Mono), không blocking event loop.
 *
 * Logic:
 *  1. Bỏ qua whitelist path (login, register, xem sản phẩm)
 *  2. Kiểm tra header Authorization: Bearer <token>
 *  3. Validate JWT qua JwtUtil (SRP)
 *  4. Inject user claims vào request header để downstream service dùng
 *  5. Trả về 401 JSON nếu token thiếu / invalid / expired
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    /** Các path không cần xác thực */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/v1/api/user/login",
            "/v1/api/user/register",
            "/v1/api/products"
    );

    // -----------------------------------------------------------------------
    // GlobalFilter
    // -----------------------------------------------------------------------

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // ① Whitelist — forward ngay, không kiểm tra
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        // ② Kiểm tra sự tồn tại và format của header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("[JWT] Missing/malformed Authorization header — path: {}", path);
            return reject(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7).trim(); // bỏ "Bearer "

        // ③ Validate JWT — wrap CPU-bound work trong Mono để không block event loop
        return Mono.fromCallable(() -> jwtUtil.extractAllClaims(token))
                .subscribeOn(Schedulers.boundedElastic())

                // ④ Token hợp lệ: inject user info vào downstream request headers
                .flatMap(claims -> chain.filter(
                        exchange.mutate()
                                .request(buildMutatedRequest(request, claims))
                                .build()
                ))

                // ⑤ Xử lý lỗi: 401 JSON gọn gàng, không để exception bubble lên
                .onErrorResume(ExpiredJwtException.class, ex -> {
                    log.warn("[JWT] Token expired — path: {}", path);
                    return reject(exchange, HttpStatus.UNAUTHORIZED, "Token has expired");
                })
                .onErrorResume(JwtException.class, ex -> {
                    log.warn("[JWT] Invalid token — path: {}", path);
                    return reject(exchange, HttpStatus.UNAUTHORIZED, "Invalid token");
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("[JWT] Unexpected authentication error — path: {}", path, ex);
                    return reject(exchange, HttpStatus.INTERNAL_SERVER_ERROR, "Authentication error");
                });
    }

    /** Chạy trước tất cả filter khác trong Gateway pipeline */
    @Override
    public int getOrder() {
        return -1;
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    /**
     * Đính kèm thông tin user (từ JWT claims) vào request headers.
     * Downstream service đọc X-User-Id, X-User-Email, X-User-Role
     * mà không cần parse JWT lại.
     */
    private ServerHttpRequest buildMutatedRequest(ServerHttpRequest original, Claims claims) {
        return original.mutate()
                .header("X-User-Id",    String.valueOf(claims.get("id")))
                .header("X-User-Email", claims.getSubject())
                .header("X-User-Role",  String.valueOf(claims.get("role")))
                .build();
    }

    /**
     * Kết thúc request với HTTP status + JSON body.
     * Không forward xuống downstream.
     */
    private Mono<Void> reject(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                status.value(), status.getReasonPhrase(), message
        );
        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }
}
