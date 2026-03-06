package com.example.spring_ecom.domain.user;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record User(
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
) {
}
