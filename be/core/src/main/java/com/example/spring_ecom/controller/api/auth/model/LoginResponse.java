package com.example.spring_ecom.controller.api.auth.model;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    String username,
    String email
) {
}
