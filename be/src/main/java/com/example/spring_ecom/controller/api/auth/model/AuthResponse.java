package com.example.spring_ecom.controller.api.auth.model;

import com.example.spring_ecom.domain.user.UserRole;
import lombok.Builder;

@Builder
public record AuthResponse(
    String accessToken,
    Long expiresIn,
    UserInfo userInfo,
    String message
) {
    public static AuthResponse of(String accessToken, Long expiresIn, UserInfo userInfo) {
        return new AuthResponse(accessToken, expiresIn, userInfo, null);
    }
    
    public record UserInfo(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        UserRole role
    ) {
    }
}
