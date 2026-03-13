package com.example.spring_ecom.controller.api.order.dao;

import java.math.BigDecimal;

public record OrderStatisticsDto(
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