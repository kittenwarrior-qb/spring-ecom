package com.example.spring_ecom.domain.statistics;

import java.math.BigDecimal;

public record RevenueByCategoryItem(
    Long categoryId,
    String categoryName,
    Long orderCount,
    BigDecimal totalRevenue,
    BigDecimal totalCost,
    BigDecimal totalProfit
) {
}

