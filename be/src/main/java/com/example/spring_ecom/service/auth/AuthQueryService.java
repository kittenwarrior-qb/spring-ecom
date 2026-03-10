package com.example.spring_ecom.service.auth;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.repository.database.user.UserEntity;
import com.example.spring_ecom.repository.database.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthQueryService {
    
    private final UserRepository userRepository;
    
    public boolean isEmailVerified(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(ResponseCode.USER_NOT_FOUND, "User not found"));
        return user.getIsEmailVerified();
    }
    
    public boolean isValidVerificationToken(String token) {
        return userRepository.findByEmailVerificationToken(token).isPresent();
    }
    
    public boolean isValidPasswordResetToken(String token) {
        return userRepository.findByPasswordResetToken(token).isPresent();
    }
}
