package com.example.spring_ecom.controller.api.user.userInfo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Request body for user info operations")
public record UserInfoRequest(
        @Schema(description = "First name", example = "Nguyễn")
        @Size(max = 50, message = "First name must not exceed 50 characters")
        String firstName,
        
        @Schema(description = "Last name", example = "Văn A")
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lastName,
        
        @Schema(description = "Phone number (1-11 digits)", example = "0901234567")
        @Pattern(regexp = "^[0-9]{1,11}$", message = "Phone number must be 1-11 digits")
        String phoneNumber,
        
        @Schema(description = "Date of birth", example = "1990-01-01")
        LocalDate dateOfBirth,
        
        @Schema(description = "Avatar image URL", example = "https://example.com/avatar.jpg")
        String avatarUrl,
        
        @Schema(description = "Address", example = "123 Đường ABC")
        @Size(max = 255, message = "Address must not exceed 255 characters")
        String address,
        
        @Schema(description = "Ward", example = "Phường 1")
        @Size(max = 100, message = "Ward must not exceed 100 characters")
        String ward,
        
        @Schema(description = "District", example = "Quận 1")
        @Size(max = 100, message = "District must not exceed 100 characters")
        String district,
        
        @Schema(description = "City", example = "TP. Hồ Chí Minh")
        @Size(max = 100, message = "City must not exceed 100 characters")
        String city,
        
        @Schema(description = "Postal code", example = "70000")
        @Size(max = 20, message = "Postal code must not exceed 20 characters")
        String postalCode
) {
}
