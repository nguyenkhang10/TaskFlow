package com.example.taskflow.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JwtFilter — chạy MỖI request, kiểm tra JWT trong header.
 * Flow:
 *  1. Đọc header "Authorization: Bearer <token>"
 *  2. Xác thực token bằng JwtUtil
 *  3. Nếu hợp lệ → đặt thông tin user vào SecurityContext
 *     (từ đó Spring Security biết "request này của ai")
 *  4. Gọi tiếp filter tiếp theo trong chain
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) { //startWith"Bearer "

            String token = authHeader.substring(7); //cắt 7 kí tự

            if (jwtUtil.isTokenValid(token)) { //xác thực
                String username = jwtUtil.extractUsername(token); //trích username

                var authentication = new UsernamePasswordAuthenticationToken(
                        username, null, List.of() //tạo token mới
                );

                // SecurityContextHolder = "bộ nhớ" lưu thông tin user trong request hiện tại
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            // Nếu token không hợp lệ → không set authentication → Spring Security sẽ chặn
        }

        // Bước 5: Chuyển request cho filter tiếp theo (hoặc controller)
        filterChain.doFilter(request, response);
    }
}
