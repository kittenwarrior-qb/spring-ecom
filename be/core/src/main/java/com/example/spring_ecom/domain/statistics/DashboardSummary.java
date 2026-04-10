package com.example.spring_ecom.domain.statistics;

import java.math.BigDecimal;

public record DashboardSummary(
    // ========== Order Overview ==========
    Long totalOrders,
    BigDecimal totalRevenue,
    BigDecimal totalCost,
    BigDecimal totalProfit,
    Double profitMargin,
    BigDecimal todayRevenue,
    BigDecimal avgOrderValue,

    // ========== Order Status Breakdown ==========
    Long pendingOrders,
    Long confirmedOrders,
    Long shippedOrders,
    Long completedOrders,
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

