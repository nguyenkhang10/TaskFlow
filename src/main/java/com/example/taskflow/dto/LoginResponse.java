package com.example.taskflow.dto;


public class LoginResponse {

    private String token;
    private String tokenType = "Bearer"; // Luôn là "Bearer" theo chuẩn OAuth2

    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
}
