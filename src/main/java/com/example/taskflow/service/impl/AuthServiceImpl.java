package com.example.taskflow.service.impl;

import com.example.taskflow.domain.User;
import com.example.taskflow.dto.LoginRequest;
import com.example.taskflow.dto.LoginResponse;
import com.example.taskflow.dto.UserResponse;
import com.example.taskflow.exception.ResourceNotFoundException;
import com.example.taskflow.mapper.UserMapper;
import com.example.taskflow.security.JwtUtil;
import com.example.taskflow.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional(readOnly = true)

    public LoginResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return new LoginResponse(token);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse me(String username) {
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return UserResponse.from(user);
    }
}