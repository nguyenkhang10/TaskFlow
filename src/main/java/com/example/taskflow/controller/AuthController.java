package com.example.taskflow.controller;

import com.example.taskflow.dto.LoginRequest;
import com.example.taskflow.dto.LoginResponse;
import com.example.taskflow.dto.UserResponse;
import com.example.taskflow.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserResponse getMe(
            @AuthenticationPrincipal String username) {
        return authService.me(username);
    }
}