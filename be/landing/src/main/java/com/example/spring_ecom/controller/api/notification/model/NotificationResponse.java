package com.example.spring_ecom.controller.api.notification.model;

public record NotificationResponse(
    boolean success,
    String message,
    String eventId
) {
    public static NotificationResponse success(String eventId) {
        return new NotificationResponse(true, "Notification sent successfully", eventId);
    }
    
    public static NotificationResponse error(String message) {
        return new NotificationResponse(false, message, null);
    }
}
