package com.example.spring_ecom.domain.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SepayTransaction(
    Long id,
    Integer sepayId,
    String gateway,
    LocalDateTime transactionDate,
    String accountNumber,
    String subAccount,
    BigDecimal amountIn,
    BigDecimal amountOut,
    BigDecimal accumulated,
    String code,
    String transactionContent,
    String referenceCode,
    String description,
    String transferType,
    BigDecimal transferAmount,
    Boolean processed,
    Long orderId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}