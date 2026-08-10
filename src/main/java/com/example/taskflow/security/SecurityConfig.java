package com.example.taskflow.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig — cấu hình toàn bộ Spring Security.
 *
 * @Configuration  : đây là class cấu hình Spring
 * @EnableWebSecurity : bật Spring Security cho web
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * SecurityFilterChain — định nghĩa luật bảo mật.
     * Mọi request HTTP đều phải qua đây.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Tắt CSRF — REST API không cần (dùng token thay vì session/cookie)
            .csrf(AbstractHttpConfigurer::disable)

            // 2. Tắt session — REST API là stateless, mỗi request tự mang token
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 3. Định nghĩa ai được vào đâu
            .authorizeHttpRequests(auth -> auth
                // /api/auth/login → PUBLIC, ai cũng gọi được (để đăng nhập lấy token)
                .requestMatchers("/api/auth/login").permitAll()
                // Mọi endpoint khác → phải có token hợp lệ
                .anyRequest().authenticated()
            )

            // 4. Đăng ký JwtFilter chạy TRƯỚC filter mặc định của Spring Security
            // (UsernamePasswordAuthenticationFilter)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCryptPasswordEncoder — dùng để:
     * - Hash password khi tạo user
     * - So sánh password khi đăng nhập
     *
     * @Bean → Spring quản lý, inject được ở bất kỳ đâu
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
