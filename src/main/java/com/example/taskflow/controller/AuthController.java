package com.example.taskflow.controller;

import com.example.taskflow.dto.*;
import com.example.taskflow.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/signup — Public endpoint.
     * Registers a new user account with encrypted password and returns a JWT access token for instant login.
     */
    @PostMapping("/signup")
    public ResponseEntity<LoginResponse> signup(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(request));
    }

    /**
     * POST /api/auth/login — Public endpoint.
     * Receives username + password, returns a JWT access token.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * POST /api/auth/register — Public endpoint (backward compatible).
     * Registers a new user account and returns user info.
     */
    @PostMapping("/register")
    public SignUpResponse register(@Valid @RequestBody SignUpRequest request) {
        return authService.register(request);
    }

    /**
     * GET /api/auth/me — Protected endpoint.
     * Returns info of the currently authenticated user.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal String username) {
        return ResponseEntity.ok(authService.me(username));
    }

    /**
     * POST /api/auth/password — Protected endpoint.
     * Checks if the provided password matches the authenticated user's current password.
     * Returns 200 (OK) if correct, throws exception / 400 Bad Request if invalid.
     */
    @PostMapping("/password")
    public ResponseEntity<Void> checkPassword(
            @AuthenticationPrincipal String username,
            @Valid @RequestBody CheckPasswordRequest request) {
        authService.checkPassword(username, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(Map.of("message",
                "Nếu email tồn tại trong hệ thống, chúng tôi đã gửi hướng dẫn đặt lại mật khẩu."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordWithTokenRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Đặt lại mật khẩu thành công"));
    }
}