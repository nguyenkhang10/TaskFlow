package com.example.taskflow.dto;

import com.example.taskflow.domain.Appeal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AppealRequest(
        @NotBlank(message = "Username cannot be blank!")
        String username,

        @NotBlank(message = "Title cannot be blank!")
        @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
        String title,

        @Size(max = 2000, message = "Content max 2000 characters")
        String content
) {
    public Appeal toDomain() {
        Appeal a = new Appeal();
        a.setUsername(username);
        a.setTitle(title);
        a.setContent(content);
        return a;
    }
}
