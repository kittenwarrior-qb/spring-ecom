package com.example.spring_ecom.controller.api.user.model;

import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.domain.userInfo.UserInfo;
import com.example.spring_ecom.service.userInfo.UserInfoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProfileResponseMapper {
    
    private final UserInfoUseCase userInfoUseCase;
    
    public UserProfileResponse toResponse(User user) {
        // Get user info from UserInfo table
        UserInfo userInfo = userInfoUseCase.findByUserId(user.id()).orElse(null);
        
        return new UserProfileResponse(
                user.id(),
                user.username(),
                user.email(),
                userInfo != null ? userInfo.firstName() : null,
                userInfo != null ? userInfo.lastName() : null,
                userInfo != null ? userInfo.phoneNumber() : null,
                userInfo != null ? userInfo.dateOfBirth() : null,
                userInfo != null ? userInfo.avatarUrl() : null,
                user.role(),
                user.isEmailVerified(),
                user.isActive(),
                user.lastLoginAt(),
                user.createdAt(),
                user.updatedAt()
        );
    }
}