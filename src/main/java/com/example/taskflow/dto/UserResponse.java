package com.example.taskflow.dto;

import com.example.taskflow.domain.User;

import java.time.OffsetDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
