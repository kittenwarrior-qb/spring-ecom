package com.example.spring_ecom.controller.api.user.model;

import com.example.spring_ecom.domain.user.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserResponse (
        Long id,
        String username,
        String email,
        String password,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate dateOfBirth,
        String avatarUrl,
        UserRole role,
        Boolean isEmailVerified,
        String emailVerificationToken,
        LocalDateTime emailVerificationTokenExpiry,
        String passwordResetToken,
        LocalDateTime passwordResetTokenExpiry,
        LocalDateTime lastLoginAt,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
){

}
