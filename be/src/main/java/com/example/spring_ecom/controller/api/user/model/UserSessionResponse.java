package com.example.spring_ecom.controller.api.user.model;

import com.example.spring_ecom.domain.user.UserRole;

public record UserSessionResponse(
        Long userId,
        String email,
        UserRole role,
        String firstName,
        String lastName,
        String phoneNumber,
        String address,
        String city,
        String district,
        String ward
) {
}