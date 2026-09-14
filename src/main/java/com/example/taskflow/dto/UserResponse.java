package com.example.taskflow.dto;

import com.example.taskflow.domain.User;
import com.example.taskflow.domain.UserRole;
import com.example.taskflow.domain.UserStatus;


import java.time.OffsetDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        String fullName,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        UserRole role,
        UserStatus status,
        OffsetDateTime deletedAt

) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getRole(),
                user.getStatus(),
                user.getDeletedAt()
        );
    }
}
