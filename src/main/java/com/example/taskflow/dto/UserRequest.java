package com.example.taskflow.dto;

import com.example.taskflow.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Username cannot be blank!")
        @Size(min = 3, max = 60, message = "Username must be between 3 and 60 characters")
        String username,

        @NotBlank(message = "Email cannot be blank!")
        @Email(message = "Invalid email format")
        @Size(max = 120, message = "Email max 120 characters")
        String email
) {
    public User toDomain() {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }
}
