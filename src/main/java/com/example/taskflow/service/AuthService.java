package com.example.taskflow.service;

import com.example.taskflow.dto.LoginRequest;
import com.example.taskflow.dto.LoginResponse;
import com.example.taskflow.dto.SignUpRequest;
import com.example.taskflow.dto.SignUpResponse;
import com.example.taskflow.dto.UserResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    UserResponse me(String username);
    SignUpResponse register(SignUpRequest request);
}