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

    private String eventId;
    private String eventType;   // CREATED | STATUS_CHANGED | CANCELLED | DELIVERED
    private Instant timestamp;
    private String source;      // "client" or "server"

    // Order payload
    private Long orderId;
    private String orderNumber;
    private Long userId;
    private String status;
    private String previousStatus;  // dùng khi STATUS_CHANGED
    private String paymentMethod;
    private String paymentStatus;
    private Double subtotal;
    private Double shippingFee;
    private Double discount;
    private Double total;
    private List<OrderItemPayload> items;
    
    // Shipping info
    private String shippingAddress;
    private String shippingCity;
    private String shippingDistrict;
    private String shippingWard;
    private String shippingPhone;
    private String recipientName;
    private String notes;

    // Event type constants
    public static final String CREATED        = "ORDER_CREATED";
    public static final String STATUS_CHANGED = "ORDER_STATUS_CHANGED";
    public static final String CANCELLED      = "ORDER_CANCELLED";
    public static final String DELIVERED      = "ORDER_DELIVERED";
    public static final String STOCK_DEDUCTED  = "STOCK_DEDUCTED";
    public static final String STOCK_RESTORED  = "STOCK_RESTORED";
    
    // New events for reservation pattern
    public static final String STOCK_RESERVED  = "STOCK_RESERVED";    // Server → Client: stock đã reserve
    public static final String STOCK_RELEASED  = "STOCK_RELEASED";    // Server → Client: stock đã release (timeout/cancel)
    public static final String ORDER_CONFIRMED = "ORDER_CONFIRMED";   // Server → Client: order OK, chờ thanh toán
    public static final String ORDER_FAILED    = "ORDER_FAILED";      // Server → Client: không đủ stock
    
    // Failure reason for ORDER_FAILED
    private String failureReason;
    private List<StockFailureItem> failedItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemPayload {
        private Long productId;
        private String productTitle;
        private Integer quantity;
        private Double price;
        private Double subtotal;  // quantity * price
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
