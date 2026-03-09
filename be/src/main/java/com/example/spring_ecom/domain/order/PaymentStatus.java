package com.example.spring_ecom.domain.order;

public enum PaymentStatus {
    UNPAID,    // Chưa thanh toán
    PENDING,   // Đang chờ xác nhận thanh toán (cho Bank transfer)
    PAID,      // Đã thanh toán
    FAILED,    // Thanh toán thất bại
    REFUNDED   // Đã hoàn tiền
}
