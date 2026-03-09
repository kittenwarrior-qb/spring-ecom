package com.example.spring_ecom.controller.api.auth.model;

import com.example.spring_ecom.domain.user.UserRole;

public record AuthResponse(
    String accessToken,
    Long expiresIn,
    UserInfo user
) {
    public static AuthResponse of(String accessToken, Long expiresIn, UserInfo user) {
        return new AuthResponse(accessToken, expiresIn, user);
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
