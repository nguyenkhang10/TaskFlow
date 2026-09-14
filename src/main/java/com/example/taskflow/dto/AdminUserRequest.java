package com.example.taskflow.dto;

import com.example.taskflow.domain.UserRole;
import jakarta.validation.constraints.Email;

public class AdminUserRequest {

    private String email;

    private String fullName;

    private UserRole role;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}