package com.example.taskflow.dto;

import com.example.taskflow.domain.Appeal;

import java.time.OffsetDateTime;

public record AppealResponse(
        Long id,
        String username,
        String title,
        String content,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static AppealResponse from(Appeal a) {
        return new AppealResponse(
                a.getId(),
                a.getUsername(),
                a.getTitle(),
                a.getContent(),
                a.getCreatedAt(),
                a.getUpdatedAt()
        );
    }
}
