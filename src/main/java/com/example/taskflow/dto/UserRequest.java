package com.example.taskflow.dto;

import com.example.taskflow.domain.User;
import com.example.taskflow.domain.UserRole;
import com.example.taskflow.domain.UserStatus;
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
        String email,

        @Size(max = 120, message = "Full name max 120 characters")
        String fullName,

        @NotBlank(message = "Password cannot be blank!")
        @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
        String password,
        UserRole role,
        UserStatus status

) {
    public User toDomain() {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(fullName != null ? fullName : username);
        user.setPasswordHash(password);
        user.setRole(role != null ? role : UserRole.MEMBER);
        user.setStatus(status != null ? status : UserStatus.ACTIVE);
        return user;
    }
}
