package com.example.spring_ecom.controller.api.user.model;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
        List<String> roles,
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
