package com.example.spring_ecom.controller.api.auth.model;

import lombok.Builder;

@Builder
public record AuthResponse(
    String accessToken,
    String refreshToken
) {
    public static AuthResponse of(String accessToken, String refreshToken) {
        return new AuthResponse(accessToken, refreshToken);
    }
}
