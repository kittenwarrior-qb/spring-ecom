package com.example.spring_ecom.kafka.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    // Event constants
    public static final String CREATED = "ORDER_CREATED";
    public static final String PAID = "ORDER_PAID";
    public static final String CANCELLED = "ORDER_CANCELLED";
    public static final String DELIVERED = "ORDER_DELIVERED";
    public static final String STATUS_CHANGED = "ORDER_STATUS_CHANGED";
    
    private String eventId;
    private Long orderId;
    private String orderNumber;
    private Long userId;
    private String eventType;
    private Instant timestamp;
    private String status;
    private String previousStatus;
    private List<OrderItemPayload> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemPayload {
        private Long productId;
        private String productTitle;
        private Integer quantity;
    }

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
