package com.example.spring_ecom.controller.api.payment.model;

import java.math.BigDecimal;

public record PaymentInfoResponse(
    String orderNumber,
    BigDecimal amount,
    String bankName,
    String accountNumber,
    String accountName,
    String transferContent,
    String qrCodeUrl,
    String qrCodeBase64,
    Long expiresIn, // seconds
    String status
) {
}