package com.example.spring_ecom.domain.auth;

public record RegisterDto(
    String username,
    String email,
    String password,
    String firstName,
    String lastName,
    String phoneNumber
) {
}
