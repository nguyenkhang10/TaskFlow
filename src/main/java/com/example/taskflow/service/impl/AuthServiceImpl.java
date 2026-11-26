package com.example.taskflow.service.impl;

import com.example.taskflow.domain.PasswordResetToken;
import com.example.taskflow.domain.User;
import com.example.taskflow.domain.UserRole;
import com.example.taskflow.domain.UserStatus;
import com.example.taskflow.dto.*;
import com.example.taskflow.exception.InvalidTokenException;
import com.example.taskflow.exception.ResourceNotFoundException;
import com.example.taskflow.mapper.PasswordResetTokenMapper;
import com.example.taskflow.mapper.UserMapper;
import com.example.taskflow.security.JwtUtil;
import com.example.taskflow.service.AuthService;
import com.example.taskflow.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordResetTokenMapper tokenMapper;
    private final EmailService emailService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.password-reset.expiration-minutes:15}")
    private long expirationMinutes;

    private static final SecureRandom RANDOM = new SecureRandom();

    public AuthServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, PasswordResetTokenMapper tokenMapper, EmailService emailService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenMapper = tokenMapper;
        this.emailService = emailService;
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

    @Override
    @Transactional
    public LoginResponse signup(SignUpRequest request) {
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

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new LoginResponse(token);
    }

    @Override
    @Transactional(readOnly = true)
    public void checkPassword(String username, CheckPasswordRequest request) {
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid password");
        }
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        // Không báo lỗi nếu email không tồn tại (tránh lộ thông tin tài khoản)
        userMapper.findByEmail(request.email().trim())
                .filter(user -> user.getStatus() == UserStatus.ACTIVE)
                .ifPresent(user -> {
                    // Vô hiệu các token cũ còn hiệu lực của user này
                    tokenMapper.invalidateAllByUserId(user.getId());

                    String rawToken = generateToken();

                    PasswordResetToken token = new PasswordResetToken();
                    token.setUserId(user.getId());
                    token.setTokenHash(sha256(rawToken));
                    token.setExpiresAt(OffsetDateTime.now().plusMinutes(expirationMinutes));
                    tokenMapper.insert(token);

                    String base = frontendUrl.endsWith("/")
                            ? frontendUrl.substring(0, frontendUrl.length() - 1) : frontendUrl;
                    String link = base + "/reset-password?token=" + rawToken;

                    emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), link);
                });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordWithTokenRequest request) {
        String errorMessage = "Link đặt lại mật khẩu không hợp lệ hoặc đã hết hạn";

        PasswordResetToken token = tokenMapper.findByTokenHash(sha256(request.token()))
                .orElseThrow(() -> new InvalidTokenException(errorMessage));

        if (token.getUsedAt() != null || token.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new InvalidTokenException(errorMessage);
        }

        // markUsed trả về 0 nếu token vừa bị request khác dùng mất
        if (tokenMapper.markUsed(token.getId()) == 0) {
            throw new InvalidTokenException(errorMessage);
        }

        User user = userMapper.findById(token.getUserId())
                .orElseThrow(() -> new InvalidTokenException(errorMessage));

        userMapper.updatePassword(user.getId(), passwordEncoder.encode(request.newPassword()));

        // Vô hiệu mọi token khác còn lại của user
        tokenMapper.invalidateAllByUserId(user.getId());
    }

    private String generateToken() {
        byte[] bytes = new byte[32];               // 256-bit ngẫu nhiên
        AuthServiceImpl.RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}

