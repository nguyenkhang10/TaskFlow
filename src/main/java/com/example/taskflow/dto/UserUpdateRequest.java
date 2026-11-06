package com.example.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserUpdateRequest(
        @Size(min = 3, max = 60, message = "Username must be between 3 and 60 characters")
        String username,

        @Email(message = "Invalid email format")
        @Size(max = 120, message = "Email max 120 characters")
        String email,

        @Size(max = 120, message = "Full name max 120 characters")
        String fullName,

        @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
        String password
) {}

