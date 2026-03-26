package com.example.spring_ecom.controller.api.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for changing password")
public record ChangePasswordRequest(
        @Schema(description = "Current password", example = "oldPassword123", required = true)
        @NotBlank(message = "Current password is required")
        String currentPassword,
        
        @Schema(description = "New password (min 6 characters)", example = "newPassword123", required = true)
        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "New password must be at least 6 characters")
        String newPassword,
        
        @Schema(description = "Confirm new password", example = "newPassword123", required = true)
        @NotBlank(message = "Confirm password is required")
        String confirmPassword
) {
}
