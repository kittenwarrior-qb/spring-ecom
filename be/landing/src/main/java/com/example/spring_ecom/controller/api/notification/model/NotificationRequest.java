package com.example.spring_ecom.controller.api.notification.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationRequest(
    @NotNull(message = "User ID is required")
    Long userId,
    
    @NotBlank(message = "Type is required")
    String type,
    
    @NotBlank(message = "Title is required")
    String title,
    
    @NotBlank(message = "Message is required")
    String message,
    
    Long referenceId,
    String referenceType,
    String imageUrl,
    String actionUrl
) {}
