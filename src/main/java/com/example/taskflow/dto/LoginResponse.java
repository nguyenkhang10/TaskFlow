package com.example.taskflow.dto;

/**
 * LoginResponse — trả về client sau khi đăng nhập thành công.
 * Chỉ chứa token (không trả password hay thông tin nhạy cảm).
 */
public class LoginResponse {

    private String token;
    private String tokenType = "Bearer"; // Luôn là "Bearer" theo chuẩn OAuth2

    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
}
