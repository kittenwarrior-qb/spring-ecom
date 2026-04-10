package com.example.spring_ecom.controller.api.admin.statistics.model;

import java.math.BigDecimal;

public record StatisticsDashboardResponse(
    // ========== Order Overview ==========
    Long totalOrders,
    BigDecimal totalRevenue,
    BigDecimal totalCost,
    BigDecimal totalProfit,
    Double profitMargin,
    BigDecimal todayRevenue,
    BigDecimal averageOrderValue,

    // ========== Order Status Breakdown ==========
    Long pendingOrders,
    Long confirmedOrders,
    Long shippedOrders,
    Long deliveredOrders,
    Long cancelledOrders,
    Long partiallyCancelledOrders,

    // ========== Inventory Overview ==========
    Long totalProducts,
    Long lowStockProducts,
    Long outOfStockProducts,
    Long totalSuppliers,
    Long pendingPurchaseOrders,
    BigDecimal inventoryValuation
) {
}

