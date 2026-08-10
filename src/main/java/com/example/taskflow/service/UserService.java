package com.example.taskflow.service;

import com.example.taskflow.dto.UserRequest;
import com.example.taskflow.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse create(UserRequest request);

    UserResponse getById(Long id);

    List<UserResponse> search(String keyword);

    UserResponse update(Long id, UserRequest request);

    void delete(Long id);
}
