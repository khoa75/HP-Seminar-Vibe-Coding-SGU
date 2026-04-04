package vn.tt.practice.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * GlobalFilter chạy trên mọi request đi qua API Gateway.
 *
 * Nhiệm vụ:
 *  1. Kiểm tra X-Correlation-ID header — nếu chưa có thì tạo UUID mới
 *  2. Gắn Correlation ID vào request header trước khi route xuống downstream service
 *  3. Gắn vào response header để client/frontend truy vết được
 *  4. Đưa vào MDC của SLF4J để mọi log tại Gateway kèm theo ID này
 *
 * Thứ tự ưu tiên: Ordered.HIGHEST_PRECEDENCE + 1
 *  → Chạy ngay sau JwtAuthenticationFilter (nếu JwtFilter dùng -1)
 *    nhưng trước tất cả các filter khác.
 */
@Slf4j
@Component
public class TrackingFilter implements GlobalFilter, Ordered {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String MDC_KEY               = "correlationId";

    @Override
    public int getOrder() {
        // Chạy trước JwtAuthenticationFilter (order = -1) → dùng -2
        return -2;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // ① Lấy hoặc sinh Correlation ID
        String correlationId = exchange.getRequest().getHeaders()
                .getFirst(CORRELATION_ID_HEADER);

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
            log.debug("[TrackingFilter] No X-Correlation-ID found — generated new: {}", correlationId);
        } else {
            log.debug("[TrackingFilter] Existing X-Correlation-ID received: {}", correlationId);
        }

        final String finalCorrelationId = correlationId;

        // ② Gắn vào request header để downstream service nhận được
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(CORRELATION_ID_HEADER, finalCorrelationId)
                .build();

        // ③ Gắn vào response header để client/frontend truy vết được
        exchange.getResponse().getHeaders()
                .add(CORRELATION_ID_HEADER, finalCorrelationId);

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        // ④ Đưa vào MDC (dùng contextWrite cho reactive — MDC không thread-safe trong WebFlux)
        //    Dùng doOnEach để set/clear MDC đúng cách trong reactive pipeline
        return chain.filter(mutatedExchange)
                .doOnEach(signal -> {
                    if (!signal.isOnComplete() || signal.isOnNext()) {
                        MDC.put(MDC_KEY, finalCorrelationId);
                    }
                })
                .doFinally(signalType -> {
                    // Luôn clear MDC sau khi request hoàn thành — tránh memory leak
                    MDC.remove(MDC_KEY);
                    log.info("[TrackingFilter] Request completed — correlationId={}, signalType={}",
                            finalCorrelationId, signalType);
                });
    }
}
