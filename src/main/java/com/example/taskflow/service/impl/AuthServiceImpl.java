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
    public UserResponse me(String username) {
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return UserResponse.from(user);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername()) //findByUsername
                .orElseThrow(() -> {
                    System.out.println("User KHÔNG TÌM ĐƯỢC");
                    return new IllegalArgumentException("Invalid username or password");
                });
        System.out.println("Username nhận được = " + request.getUsername());
        System.out.println("Password nhận được = " + request.getPassword());
        System.out.println("User tìm được = " + user.getUsername());
        System.out.println("Email = " + user.getEmail());
        System.out.println("Hash từ DB = " + user.getPasswordHash());

        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        System.out.println("Password match result = " + matches);

        if (!matches) {
            System.out.println("Invalid password");
            throw new IllegalArgumentException("Invalid username or password");
        }
        System.out.println("Password KHỚP");

        String token = jwtUtil.generateToken(user.getUsername());
        return new LoginResponse(token);
    }
}