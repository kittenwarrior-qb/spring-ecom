package com.example.spring_ecom.controller.api.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
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
