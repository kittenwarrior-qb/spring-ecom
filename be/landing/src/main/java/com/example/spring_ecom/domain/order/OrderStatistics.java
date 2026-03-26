package com.example.spring_ecom.domain.order;

import java.math.BigDecimal;

public record OrderStatistics(
    Long totalOrders,
    Long pendingOrders,
    Long confirmedOrders,
    Long shippedOrders,
    Long deliveredOrders,
    Long cancelledOrders,
    Long partiallyCancelledOrders,
    BigDecimal totalRevenue,
    BigDecimal todayRevenue
) {
}
