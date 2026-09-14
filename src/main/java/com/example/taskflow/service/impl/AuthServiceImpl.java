package com.example.taskflow.service.impl;

import com.example.taskflow.domain.User;
import com.example.taskflow.domain.UserRole;
import com.example.taskflow.domain.UserStatus;
import com.example.taskflow.dto.LoginRequest;
import com.example.taskflow.dto.LoginResponse;
import com.example.taskflow.dto.SignUpRequest;
import com.example.taskflow.dto.SignUpResponse;
import com.example.taskflow.dto.UserResponse;
import com.example.taskflow.exception.ResourceNotFoundException;
import com.example.taskflow.mapper.UserMapper;
import com.example.taskflow.security.JwtUtil;
import com.example.taskflow.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

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
    public UserResponse me(String username) {
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return UserResponse.from(user);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new LoginResponse(token);
    }

    @Override
    @Transactional
    public SignUpResponse register(SignUpRequest request) {
        if (userMapper.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userMapper.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.MEMBER);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());

        userMapper.insert(user);
        return new SignUpResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}