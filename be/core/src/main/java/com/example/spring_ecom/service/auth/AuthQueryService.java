package com.example.spring_ecom.service.auth;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.service.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthQueryService {
    
    private final UserUseCase userUseCase;

    protected boolean isEmailVerified(String email) {
        User user = userUseCase.findByEmail(email)
                .orElseThrow(() -> new BaseException(ResponseCode.USER_NOT_FOUND, "User not found"));
        return user.isEmailVerified();
    }
    
    protected boolean isValidVerificationToken(String token) {
        return userUseCase.findByEmailVerificationToken(token).isPresent();
    }
    
    protected boolean isValidPasswordResetToken(String token) {
        return userUseCase.findByPasswordResetToken(token).isPresent();
    }
}
