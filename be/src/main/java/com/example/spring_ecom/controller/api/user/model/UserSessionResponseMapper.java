package com.example.spring_ecom.controller.api.user.model;

import com.example.spring_ecom.domain.user.UserRole;
import com.example.spring_ecom.domain.userInfo.UserInfo;
import com.example.spring_ecom.repository.redis.session.RedisEntity;
import com.example.spring_ecom.service.userInfo.UserInfoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSessionResponseMapper {
    
    private final UserInfoUseCase userInfoUseCase;
    
    public UserSessionResponse toResponse(RedisEntity session) {
        // Get user info from UserInfo table instead of Redis session
        UserInfo userInfo = userInfoUseCase.findByUserId(session.getUserId()).orElse(null);
        
        return new UserSessionResponse(
                session.getUserId(),
                session.getEmail(),
                UserRole.valueOf(session.getRole()),
                userInfo != null ? userInfo.firstName() : null,
                userInfo != null ? userInfo.lastName() : null,
                userInfo != null ? userInfo.phoneNumber() : null,
                userInfo != null ? userInfo.address() : null,
                userInfo != null ? userInfo.city() : null,
                userInfo != null ? userInfo.district() : null,
                userInfo != null ? userInfo.ward() : null
        );
    }
}