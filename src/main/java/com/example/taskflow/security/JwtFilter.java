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
 *
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

        // Bước 1: Đọc header Authorization
        String authHeader = request.getHeader("Authorization");

        // Bước 2: Kiểm tra có đúng định dạng "Bearer <token>" không
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Cắt bỏ "Bearer " (7 ký tự) để lấy token thực sự
            String token = authHeader.substring(7);

            // Bước 3: Xác thực token
            if (jwtUtil.isTokenValid(token)) {
                String username = jwtUtil.extractUsername(token);

                // Bước 4: Tạo Authentication object và đặt vào SecurityContext
                // - principal   : username (ai đang đăng nhập)
                // - credentials : null (không cần password nữa, đã có token)
                // - authorities  : List.of() (chưa có role, sẽ thêm sau)
                var authentication = new UsernamePasswordAuthenticationToken(
                        username, null, List.of()
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
