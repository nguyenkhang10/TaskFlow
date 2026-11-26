package com.example.taskflow.service.impl;

import com.example.taskflow.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class BrevoEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(BrevoEmailService.class);
    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${mail.brevo-api-key:}")
    private String apiKey;

    @Value("${mail.sender-email:}")
    private String senderEmail;

    @Value("${mail.sender-name:TaskFlow}")
    private String senderName;

    @Value("${app.password-reset.expiration-minutes:15}")
    private long expirationMinutes;

    @Async // gửi ở thread riêng để API trả về nhanh
    @Override
    public void sendPasswordResetEmail(String toEmail, String fullName, String resetLink) {
        // Chế độ DEV: chưa cấu hình API key thì chỉ in link ra log để test
        if (apiKey == null || apiKey.isBlank()) {
            log.info("[DEV] Password reset link for {}: {}", toEmail, resetLink);
            return;
        }

        String html = """
                <p>Xin chào %s,</p>
                <p>Bạn vừa yêu cầu đặt lại mật khẩu TaskFlow. Bấm vào nút bên dưới để tiếp tục
                (link có hiệu lực trong %d phút):</p>
                <p><a href="%s" style="background:#4f46e5;color:#fff;padding:10px 18px;
                   border-radius:6px;text-decoration:none">Đặt lại mật khẩu</a></p>
                <p>Nếu bạn không yêu cầu, hãy bỏ qua email này.</p>
                """.formatted(HtmlUtils.htmlEscape(fullName), expirationMinutes, resetLink);

        try {
            Map<String, Object> payload = Map.of(
                    "sender", Map.of("name", senderName, "email", senderEmail),
                    "to", List.of(Map.of("email", toEmail)),
                    "subject", "Đặt lại mật khẩu TaskFlow",
                    "htmlContent", html
            );

            HttpRequest request = HttpRequest.newBuilder(URI.create(BREVO_URL))
                    .header("api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .header("accept", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                log.error("Brevo gửi mail thất bại: {} - {}", response.statusCode(), response.body());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Gửi mail bị gián đoạn", e);
        } catch (Exception e) {
            log.error("Gửi mail thất bại", e);
        }
    }
}