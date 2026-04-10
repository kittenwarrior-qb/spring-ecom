package com.example.spring_ecom.controller.api.admin.statistics.model;

import java.math.BigDecimal;

public record RevenueByCategoryResponse(
    Long categoryId,
    String categoryName,
    Long orderCount,
    BigDecimal totalRevenue,
    BigDecimal totalCost,
    BigDecimal totalProfit
) {
}

