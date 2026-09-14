package com.example.taskflow.service;

import com.example.taskflow.dto.UserRequest;
import com.example.taskflow.dto.UserResponse;
import com.example.taskflow.domain.UserRole;
import com.example.taskflow.domain.UserStatus;
import com.example.taskflow.dto.ResetPasswordRequest;

import java.util.List;

public interface UserService {
    UserResponse create(UserRequest request);

    UserResponse getById(Long id);

    List<UserResponse> search(String keyword, UserRole role);

    List<UserResponse> searchAdmin(String username, String email, UserRole role, UserStatus status);

    UserResponse update(Long id, UserRequest request);

    void resetPassword(Long id, ResetPasswordRequest request);

    void softDelete(Long id);

    void delete(Long id);
}
