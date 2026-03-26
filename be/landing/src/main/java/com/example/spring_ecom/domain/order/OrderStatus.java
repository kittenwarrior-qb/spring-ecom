package com.example.spring_ecom.domain.order;

public enum OrderStatus {
    PENDING,           // Order vừa tạo, chưa xử lý stock
    PENDING_STOCK,     // Đang chờ reserve stock (gửi Kafka event)
    STOCK_RESERVED,    // Stock đã được reserve, chờ thanh toán
    STOCK_FAILED,      // Không đủ stock, order failed
    CONFIRMED,         // Đã thanh toán, chờ ship
    SHIPPED,           // Đang giao
    DELIVERED,         // Đã giao
    CANCELLED,         // Đã hủy
    PARTIALLY_CANCELLED  
}
