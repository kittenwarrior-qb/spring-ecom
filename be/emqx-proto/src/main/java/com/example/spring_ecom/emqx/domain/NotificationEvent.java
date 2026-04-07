package com.example.spring_ecom.emqx.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * MQTT event for real-time notifications
 * Used for pub/sub via EMQX broker
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private String eventId;
    private String eventType;       // ORDER_CREATED, ORDER_STATUS_CHANGED, PROMOTION, SYSTEM
    private Instant timestamp;
    private String source;          // "server" - always from server for notifications

    // Notification payload
    private Long notificationId;
    private Long userId;
    private String type;
    private String title;
    private String message;
    private Long referenceId;
    private String referenceType;   // ORDER, PRODUCT, PROMOTION, etc.
    private String imageUrl;
    private String actionUrl;
    private Boolean isRead;
    private LocalDateTime createdAt;

    // Event type constants
    public static final String ORDER_CREATED = "ORDER_CREATED";
    public static final String ORDER_STATUS_CHANGED = "ORDER_STATUS_CHANGED";
    public static final String ORDER_CONFIRMED = "ORDER_CONFIRMED";
    public static final String ORDER_SHIPPED = "ORDER_SHIPPED";
    public static final String ORDER_DELIVERED = "ORDER_DELIVERED";
    public static final String ORDER_CANCELLED = "ORDER_CANCELLED";
    public static final String PROMOTION = "PROMOTION";
    public static final String SYSTEM = "SYSTEM";
    public static final String STOCK_ALERT = "STOCK_ALERT";
    public static final String PAYMENT_SUCCESS = "PAYMENT_SUCCESS";
    public static final String PAYMENT_FAILED = "PAYMENT_FAILED";

    /**
     * Topic pattern for user-specific notifications
     * Format: notifications/{userId}/{type}
     */
    public static String topicForUser(Long userId, String type) {
        return String.format("notifications/%d/%s", userId, type.toLowerCase());
    }

    /**
     * Topic pattern for user's all notifications
     * Format: notifications/{userId}/#
     */
    public static String topicPatternForUser(Long userId) {
        return String.format("notifications/%d/#", userId);
    }

    /**
     * Topic for broadcast notifications
     * Format: notifications/broadcast/{type}
     */
    public static String topicForBroadcast(String type) {
        return String.format("notifications/broadcast/%s", type.toLowerCase());
    }
}
