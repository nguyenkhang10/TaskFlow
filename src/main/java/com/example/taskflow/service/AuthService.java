package com.example.taskflow.service;

import com.example.taskflow.dto.*;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    UserResponse me(String username);

    SignUpResponse register(SignUpRequest request);

    LoginResponse signup(SignUpRequest request);

    void checkPassword(String username, CheckPasswordRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordWithTokenRequest request);
}