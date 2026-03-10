package com.example.spring_ecom.controller.api.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Request body for updating user profile")
public record UpdateProfileRequest(
        @Schema(description = "First name", example = "Nguyễn")
        @Size(max = 50, message = "First name must not exceed 50 characters")
        String firstName,
        
        @Schema(description = "Last name", example = "Văn A")
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lastName,
        
        @Schema(description = "Phone number (10-11 digits)", example = "0901234567")
        @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone number must be 10-11 digits")
        String phoneNumber,
        
        @Schema(description = "Date of birth", example = "1990-01-01")
        LocalDate dateOfBirth
) {
}
