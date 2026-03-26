package com.example.spring_ecom.controller.api.review.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for admin reply to a review")
public record AdminReplyRequest(
        @Schema(description = "Admin reply message", example = "Thank you for your feedback!", required = true)
        @NotBlank(message = "Reply message is required")
        @Size(max = 1000, message = "Reply must not exceed 1000 characters")
        String reply
) {
}
