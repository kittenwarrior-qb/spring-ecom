package com.example.spring_ecom.controller.api.user.model;

import com.example.spring_ecom.domain.user.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "User profile response (without sensitive data)")
public record UserProfileResponse(
        @Schema(description = "User ID", example = "1")
        Long id,
        
        @Schema(description = "Username", example = "john_doe")
        String username,
        
        @Schema(description = "Email address", example = "john@example.com")
        String email,
        
        @Schema(description = "First name", example = "John")
        String firstName,
        
        @Schema(description = "Last name", example = "Doe")
        String lastName,
        
        @Schema(description = "Phone number", example = "0901234567")
        String phoneNumber,
        
        @Schema(description = "Date of birth", example = "1990-01-01")
        LocalDate dateOfBirth,
        
        @Schema(description = "Avatar URL")
        String avatarUrl,
        
        @Schema(description = "Address", example = "123 Đường ABC")
        String address,
        
        @Schema(description = "Ward", example = "Phường 1")
        String ward,
        
        @Schema(description = "District", example = "Quận 1")
        String district,
        
        @Schema(description = "City", example = "TP. Hồ Chí Minh")
        String city,
        
        @Schema(description = "Postal code", example = "70000")
        String postalCode,
        
        @Schema(description = "User role", example = "USER")
        UserRole role,
        
        @Schema(description = "Email verification status", example = "true")
        Boolean isEmailVerified,
        
        @Schema(description = "Account active status", example = "true")
        Boolean isActive,
        
        @Schema(description = "Last login timestamp")
        LocalDateTime lastLoginAt,
        
        @Schema(description = "Account creation timestamp")
        LocalDateTime createdAt,
        
        @Schema(description = "Last update timestamp")
        LocalDateTime updatedAt
) {
}
