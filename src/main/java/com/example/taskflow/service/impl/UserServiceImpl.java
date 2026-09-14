package com.example.taskflow.service.impl;

import com.example.taskflow.domain.User;
import com.example.taskflow.domain.UserRole;
import com.example.taskflow.domain.UserStatus;
import com.example.taskflow.dto.ResetPasswordRequest;
import com.example.taskflow.dto.UserRequest;
import com.example.taskflow.dto.UserResponse;
import com.example.taskflow.exception.ResourceNotFoundException;
import com.example.taskflow.mapper.UserMapper;
import com.example.taskflow.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {
        User user = request.toDomain();
        if (user.getPasswordHash() != null && !user.getPasswordHash().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        userMapper.insert(user);
        return getById(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user id=" + id));
        return UserResponse.from(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> search(String keyword, UserRole role) {
        return userMapper.search(keyword, role)
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> searchAdmin(String username, String email, UserRole role, UserStatus status) {
        return userMapper.searchAdmin(username, email, role, status)
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User existing = userMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user id=" + id));

        User user = request.toDomain();
        user.setId(id);
        if (user.getPasswordHash() != null && !user.getPasswordHash().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        } else {
            user.setPasswordHash(existing.getPasswordHash());
        }

        int affected = userMapper.update(user);
        if (affected == 0) {
            throw new ResourceNotFoundException("Không tìm thấy user id=" + id);
        }
        return getById(id);
    }

    @Override
    @Transactional
    public void resetPassword(Long id, ResetPasswordRequest request) {
        User user = userMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user id=" + id));

        String encoded = passwordEncoder.encode(request.getNewPassword());
        userMapper.updatePassword(id, encoded);
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        User user = userMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user id=" + id));

        userMapper.softDeleteById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        int affected = userMapper.deleteById(id);
        if (affected == 0) {
            throw new ResourceNotFoundException("Không tìm thấy user id=" + id);
        }
    }
}
