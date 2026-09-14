package com.example.taskflow.dto;


public class LoginResponse {

    private String token;
    private String tokenType = "Bearer"; // Luôn là "Bearer" theo chuẩn OAuth2
    private String role;  // ← Thêm (tuỳ chọn)


    public LoginResponse(String token) {
        this.token = token;
        this.role = role;
    }

    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
    public String getRole() { return role; }
}
