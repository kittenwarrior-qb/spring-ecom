package com.example.spring_ecom.emqx.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private String eventId;
    private String eventType;
    private Instant timestamp;
    private String source;

    // Notification payload
    private Long notificationId;
    private Long userId;
    private String type;
    private String title;
    private String message;
    private Long referenceId;
    private String referenceType;
    private String imageUrl;
    private String actionUrl;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
