package com.example.taskflow.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * LoginRequest — dữ liệu client gửi lên khi đăng nhập.
 * @NotBlank đảm bảo không được để trống (Spring Validation).
 */
public class LoginRequest {

    @NotBlank(message = "Username không được để trống")
    private String username;

    @NotBlank(message = "Password không được để trống")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
