package com.example.spring_ecom.domain.notification;

public record Notification(
    Long id,
    Long userId,
    String type,
    String title,
    String message,
    Long referenceId,
    String referenceType,
    String imageUrl,
    String actionUrl,
    Boolean isRead,
    java.time.LocalDateTime createdAt
) {
}
