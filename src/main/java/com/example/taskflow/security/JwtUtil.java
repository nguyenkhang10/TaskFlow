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

    /**
     * Tạo JWT token từ username.
     * Token sẽ hết hạn sau `expirationMs` mili-giây.
     */
    public String generateToken(String username) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)          // Payload: "sub" = username
                .issuedAt(now)              // Payload: "iat" = thời điểm tạo
                .expiration(expiry)         // Payload: "exp" = thời điểm hết hạn
                .signWith(secretKey)        // Ký bằng secret key → tạo Signature
                .compact();                 // Ghép Header.Payload.Signature → chuỗi JWT
    }

    /**
     * Đọc username từ JWT token.
     * Nếu token không hợp lệ → ném JwtException.
     */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Kiểm tra token có hợp lệ không.
     * Trả về false nếu: chữ ký sai, token hết hạn, hoặc bị giả mạo.
     */
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token); // Nếu không ném exception → hợp lệ
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;       // Token hỏng, hết hạn, hoặc chữ ký sai
        }
    }

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
