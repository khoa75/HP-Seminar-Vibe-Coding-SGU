package vn.tt.practice.apigateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * JwtUtil — Single Responsibility: chỉ xử lý logic JWT.
 *
 * Sử dụng JJWT 0.12.x API:
 *  - Jwts.parser().verifyWith(key).build().parseSignedClaims(token)   ← không deprecated
 *  - .getPayload()                                                     ← thay getBody()
 *  - .signWith(key)                                                    ← không cần truyền Algorithm
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    /**
     * Tạo SecretKey từ secret string (UTF-8 bytes).
     * Key phải ≥ 32 bytes (256 bits) cho HS256.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Parse token và trả về Claims (payload).
     *
     * @throws ExpiredJwtException   token đã hết hạn
     * @throws JwtException          token sai format, sai chữ ký, v.v.
     */
    public Claims extractAllClaims(String token) {
        // JJWT 0.12.x: verifyWith + parseSignedClaims + getPayload
        return Jwts.parser()
                .verifyWith(getSigningKey())   // thay setSigningKey() (deprecated)
                .build()
                .parseSignedClaims(token)      // thay parseClaimsJws() (deprecated)
                .getPayload();                 // thay getBody() (deprecated)
    }

    /**
     * Kiểm tra nhanh: token có hợp lệ không (không ném exception).
     */
    public boolean isValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
