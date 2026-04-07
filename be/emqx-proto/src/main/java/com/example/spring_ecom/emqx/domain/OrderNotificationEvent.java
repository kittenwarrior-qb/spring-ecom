package com.example.spring_ecom.emqx.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * MQTT event for order-related real-time notifications
 * Contains detailed order information for client display
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotificationEvent {

    private String eventId;
    private String eventType;       // ORDER_CREATED, ORDER_STATUS_CHANGED, etc.
    private Instant timestamp;
    private String source;          // "server"

    // Order notification payload
    private Long orderId;
    private String orderNumber;
    private Long userId;
    private String status;
    private String previousStatus;
    private String title;
    private String message;
    private String actionUrl;
    
    // Order summary for quick display
    private Double total;
    private Integer itemCount;
    private String paymentMethod;
    private String paymentStatus;
    
    // Shipping info
    private String shippingAddress;
    private String shippingCity;
    private String recipientName;
    private String recipientPhone;

    // Estimated delivery (for SHIPPED status)
    private String estimatedDelivery;

    // Failure reason (for ORDER_FAILED)
    private String failureReason;
    private List<StockFailureItem> failedItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockFailureItem {
        private Long productId;
        private String productTitle;
        private Integer requestedQuantity;
        private Integer availableQuantity;
    }
}
