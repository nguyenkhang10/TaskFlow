package com.example.taskflow.controller;

import com.example.taskflow.domain.User;
import com.example.taskflow.dto.LoginRequest;
import com.example.taskflow.dto.LoginResponse;
import com.example.taskflow.dto.UserResponse;
import com.example.taskflow.mapper.UserMapper;
import com.example.taskflow.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * AuthController — xử lý đăng nhập và lấy thông tin user hiện tại.
 * Lưu ý: Controller này KHÔNG qua AuthService vì logic đơn giản.
 * Trong project thực tế lớn hơn, nên tách ra AuthService.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserMapper      userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil         jwtUtil;

    public AuthController(UserMapper userMapper,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userMapper      = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil         = jwtUtil;
    }

    /**
     * POST /api/auth/login
     * Public — không cần token.
     *
     * Flow:
     *  1. Tìm user theo username trong DB
     *  2. So sánh password gửi lên với BCrypt hash trong DB
     *  3. Nếu khớp → tạo JWT và trả về
     *  4. Nếu không → trả 401 Unauthorized
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        User user = userMapper.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Username hoặc password không đúng"));
        }

        String token = jwtUtil.generateToken(user.getUsername());

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {

        String username = authentication.getName();

        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tìm thấy"));

        return ResponseEntity.ok(Map.of(
                "id",       user.getId(),
                "username", user.getUsername(),
                "email",    user.getEmail(),
                "fullName", user.getFullName()
        ));
    }
}
