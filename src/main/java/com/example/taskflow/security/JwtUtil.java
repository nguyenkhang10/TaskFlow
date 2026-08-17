package com.example.taskflow.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JwtUtil — chịu trách nhiệm TẠO và XÁC THỰC JWT token.
 *
 * Cách JWT hoạt động:
 *   Header.Payload.Signature
 *   - Header   : thuật toán (HS256)
 *   - Payload  : data (username, expiration)
 *   - Signature: HMAC(header + payload, secretKey) → không ai giả mạo được
 */
@Component
public class JwtUtil {

    // Secret key đọc từ application.yml — phải đủ dài (>= 32 ký tự)
    private final SecretKey secretKey;

    // Thời gian sống của token (mili-giây), đọc từ application.yml
    private final long expirationMs;

    // Spring tự inject giá trị từ application.yml qua @Value
    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        // Keys.hmacShaKeyFor() tạo SecretKey từ chuỗi bytes
        this.secretKey   = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }   //tạo token

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }   //kiểm tra token hợp lệ không

    // --- Private helper ---

    /**
     * Parse token → Claims (toàn bộ payload).
     * JJWT tự kiểm tra: chữ ký + thời hạn — nếu sai → ném exception ngay.
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)  // Dùng secret key để xác thực chữ ký
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
