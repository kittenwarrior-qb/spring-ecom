package com.example.spring_ecom.domain.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Order(
    Long id,
    String orderNumber,
    Long userId,
    OrderStatus status,
    PaymentStatus paymentStatus,
    BigDecimal subtotal,
    BigDecimal shippingFee,
    BigDecimal discount,
    BigDecimal total,
    PaymentMethod paymentMethod,
    String shippingAddress,
    String shippingCity,
    String shippingDistrict,
    String shippingWard,
    String recipientName,
    String recipientPhone,
    String note,
    Long couponId,
    String couponCode,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime cancelledAt
) {
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Long id;
        private String orderNumber;
        private Long userId;
        private OrderStatus status;
        private PaymentStatus paymentStatus;
        private BigDecimal subtotal;
        private BigDecimal shippingFee;
        private BigDecimal discount;
        private BigDecimal total;
        private PaymentMethod paymentMethod;
        private String shippingAddress;
        private String shippingCity;
        private String shippingDistrict;
        private String shippingWard;
        private String recipientName;
        private String recipientPhone;
        private String note;
        private Long couponId;
        private String couponCode;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime cancelledAt;
        
        public Builder id(Long id) { this.id = id; return this; }
        public Builder orderNumber(String orderNumber) { this.orderNumber = orderNumber; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder status(OrderStatus status) { this.status = status; return this; }
        public Builder paymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; return this; }
        public Builder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }
        public Builder shippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; return this; }
        public Builder discount(BigDecimal discount) { this.discount = discount; return this; }
        public Builder total(BigDecimal total) { this.total = total; return this; }
        public Builder paymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public Builder shippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; return this; }
        public Builder shippingCity(String shippingCity) { this.shippingCity = shippingCity; return this; }
        public Builder shippingDistrict(String shippingDistrict) { this.shippingDistrict = shippingDistrict; return this; }
        public Builder shippingWard(String shippingWard) { this.shippingWard = shippingWard; return this; }
        public Builder recipientName(String recipientName) { this.recipientName = recipientName; return this; }
        public Builder recipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; return this; }
        public Builder note(String note) { this.note = note; return this; }
        public Builder couponId(Long couponId) { this.couponId = couponId; return this; }
        public Builder couponCode(String couponCode) { this.couponCode = couponCode; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder cancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; return this; }
        
        public Order build() {
            return new Order(id, orderNumber, userId, status, paymentStatus, subtotal, shippingFee, discount, total, paymentMethod, shippingAddress, shippingCity, shippingDistrict, shippingWard, recipientName, recipientPhone, note, couponId, couponCode, createdAt, updatedAt, cancelledAt);
        }
    }
}
