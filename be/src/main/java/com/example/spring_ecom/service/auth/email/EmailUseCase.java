package com.example.spring_ecom.service.auth.email;

public interface EmailUseCase {
    void sendVerificationEmail(Long userId);
    void verifyEmail(String token);
    void resendVerificationEmail(String email);
    boolean isEmailVerified(String email);
}