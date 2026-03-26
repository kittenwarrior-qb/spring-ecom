package com.example.spring_ecom.repository.database.order.dao;

import java.math.BigDecimal;

public record OrderStatisticsDao(
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