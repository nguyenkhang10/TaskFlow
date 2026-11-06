package com.example.taskflow.dto;

import jakarta.validation.constraints.NotBlank;

public class CheckPasswordRequest {

    @NotBlank(message = "Password không được để trống")
    private String password;

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
