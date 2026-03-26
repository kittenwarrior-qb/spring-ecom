package com.example.spring_ecom.service.auth.email;

import com.example.spring_ecom.service.auth.AuthQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailUseCaseService implements EmailUseCase {
    
    private final EmailCommandService commandService;
    private final AuthQueryService authQueryService;
    
    @Override
    public void sendVerificationEmail(Long userId) {
        commandService.sendVerificationEmail(userId);
    }
    
    @Override
    public void verifyEmail(String token) {
        commandService.verifyEmail(token);
    }
    
    @Override
    public void resendVerificationEmail(String email) {
        commandService.resendVerificationEmail(email);
    }
    
    @Override
    public boolean isEmailVerified(String email) {
        return authQueryService.isEmailVerified(email);
    }
}