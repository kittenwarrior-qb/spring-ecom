package com.example.spring_ecom.controller.api.admin.statistics.model;

import java.math.BigDecimal;

public record TopSellingProductResponse(
    Long productId,
    String productTitle,
    Long totalSold,
    BigDecimal totalRevenue,
    BigDecimal totalProfit
) {
}

