package com.example.taskflow.controller;

import com.example.taskflow.dto.LoginRequest;
import com.example.taskflow.dto.LoginResponse;
import com.example.taskflow.dto.SignUpRequest;
import com.example.taskflow.dto.SignUpResponse;
import com.example.taskflow.dto.UserResponse;
import com.example.taskflow.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/register")
    public SignUpResponse register(@Valid @RequestBody SignUpRequest request) {
        return authService.register(request);
    }

    @GetMapping("/me")
    public UserResponse getMe(
            @AuthenticationPrincipal String username) {
        return authService.me(username);
    }
}