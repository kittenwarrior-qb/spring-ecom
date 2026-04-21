package com.example.spring_ecom.service.auth.email;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.service.auth.AuthUseCase;
import com.example.spring_ecom.service.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailCommandService {
    
    private final UserUseCase userUseCase;
    private final AuthUseCase authUseCase;
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${app.resend.api-key}")
    private String apiKey;
    
    @Value("${app.resend.from-email}")
    private String fromEmail;
    
    @Value("${app.frontend.url}")
    private String frontendUrl;
    
    @Transactional
    public void sendVerificationEmail(Long userId) {
        User user = userUseCase.findByUserId(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.USER_NOT_FOUND, "User not found"));
        
        if (user.isEmailVerified()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Email is already verified");
        }
        
        // Generate verification token
        String verificationToken = UUID.randomUUID().toString();
        LocalDateTime expiryTime = LocalDateTime.now().plusHours(24);
        
        // Update user with verification token via UseCase
        userUseCase.setEmailVerificationToken(userId, verificationToken, expiryTime);

        // Send email
        sendEmail(user.email(), "Xác thực email của bạn", buildEmailVerificationTemplate(user.username(), verificationToken));

        log.info("Verification email sent to user: {}", user.email());
    }
    
    @Transactional
    public void verifyEmail(String token) {
        User user = userUseCase.findByEmailVerificationToken(token)
                .orElseThrow(() -> new BaseException(ResponseCode.BAD_REQUEST, "Invalid verification token"));
        
        if (user.emailVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Verification token has expired");
        }
        
        if (user.isEmailVerified()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Email is already verified");
        }
        
        // Mark email as verified via UseCase
        userUseCase.markEmailVerified(user.id());

        log.info("Email verified successfully for user: {}", user.email());
    }
    
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userUseCase.findByEmail(email)
                .orElseThrow(() -> new BaseException(ResponseCode.USER_NOT_FOUND, "User not found"));
        
        if (authUseCase.isEmailVerified(email)) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Email is already verified");
        }
        
        sendVerificationEmail(user.id());
    }
    
    private void sendEmail(String toEmail, String subject, String htmlContent) {
        try {
            // Prepare request headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            
            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("from", fromEmail);
            requestBody.put("to", new String[]{toEmail});
            requestBody.put("subject", subject);
            requestBody.put("html", htmlContent);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // Send request to Resend API
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(
                "https://api.resend.com/emails", 
                request, 
                (Class<Map<String, Object>>) (Class<?>) Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                String emailId = Objects.nonNull(responseBody) ? (String) responseBody.get("id") : "unknown";
                log.info("Email sent successfully to {} with ID: {}", toEmail, emailId);
            } else {
                log.error("Failed to send email to {}: HTTP {}", toEmail, response.getStatusCode());
                throw new RuntimeException("Failed to send email");
            }
            
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    private String buildEmailVerificationTemplate(String username, String verificationToken) {
        String verificationUrl = frontendUrl + "/verify-email?token=" + verificationToken;
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Xác thực email</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #2c3e50;">Chào mừng bạn đến với Fahaza!</h2>
                    
                    <p>Xin chào <strong>%s</strong>,</p>
                    
                    <p>Cảm ơn bạn đã đăng ký tài khoản. Để hoàn tất quá trình đăng ký, vui lòng xác thực địa chỉ email của bạn bằng cách nhấp vào nút bên dưới:</p>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" 
                           style="background-color: #3498db; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">
                            Xác thực Email
                        </a>
                    </div>
                    
                    <p>Hoặc bạn có thể copy và paste link sau vào trình duyệt:</p>
                    <p style="word-break: break-all; background-color: #f8f9fa; padding: 10px; border-radius: 3px;">
                        %s
                    </p>
                    
                    <p><strong>Lưu ý:</strong> Link xác thực này sẽ hết hạn sau 24 giờ.</p>
                    
                    <p>Nếu bạn không đăng ký tài khoản này, vui lòng bỏ qua email này.</p>
                    
                    <hr style="margin: 30px 0; border: none; border-top: 1px solid #eee;">
                    <p style="font-size: 12px; color: #666;">
                        Email này được gửi từ Fahaza. Vui lòng không trả lời email này.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(username, verificationUrl, verificationUrl);
    }
}

