package com.example.spring_ecom.domain.statistics;

import java.math.BigDecimal;

public record TopSellingProduct(
    Long productId,
    String productTitle,
    Long totalSold,
    BigDecimal totalRevenue,
    BigDecimal totalProfit
) {
}

