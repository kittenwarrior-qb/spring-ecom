package com.example.spring_ecom.controller.api.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for updating avatar URL")
public record UpdateAvatarRequest(
        @Schema(description = "Avatar image URL", example = "https://example.com/avatar.jpg", required = true)
        @NotBlank(message = "Avatar URL is required")
        String avatarUrl
) {
}
