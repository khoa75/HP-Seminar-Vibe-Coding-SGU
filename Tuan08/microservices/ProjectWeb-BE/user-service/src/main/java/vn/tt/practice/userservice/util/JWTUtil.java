package vn.tt.practice.userservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.tt.practice.userservice.dto.UserDTO;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JwtUtil cho user-service — JJWT 0.12.x API (không deprecated).
 */
@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;

    private final long expiration = 86400000; // 24h ms

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDTO user) {
        return Jwts.builder()
                .subject(user.getEmail())                           // 0.12.x: subject()
                .claim("role", user.getIsAdmin())
                .claim("id", user.getId())
                .claim("username", user.getUsername())
                .issuedAt(new Date())                              // 0.12.x: issuedAt()
                .expiration(new Date(System.currentTimeMillis() + expiration)) // 0.12.x: expiration()
                .signWith(getSigningKey())                          // 0.12.x: chỉ cần key, tự suy ra HS256
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())          // 0.12.x: verifyWith()
                .build()
                .parseSignedClaims(token)             // 0.12.x: parseSignedClaims()
                .getPayload();                        // 0.12.x: getPayload()
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
