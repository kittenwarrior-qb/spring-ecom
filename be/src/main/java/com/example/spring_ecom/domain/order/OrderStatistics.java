package com.example.spring_ecom.domain.order;

import java.math.BigDecimal;

public record OrderStatistics(
    Long totalOrders,
    Long pendingOrders,
    Long processingOrders,
    Long shippedOrders,
    Long deliveredOrders,
    Long cancelledOrders,
    BigDecimal totalRevenue,
    BigDecimal todayRevenue
) {
}
