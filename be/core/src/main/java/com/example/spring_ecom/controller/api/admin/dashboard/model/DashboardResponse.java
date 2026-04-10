package com.example.spring_ecom.controller.api.admin.dashboard.model;

import com.example.spring_ecom.domain.order.OrderStatistics;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
    // ========== Order Overview ==========
    Long totalOrders,
    Double totalRevenue,
    Double totalCost,
    Double totalProfit,
    Double profitMargin,
    Double todayRevenue,

    // ========== Order Status Breakdown ==========
    Long pendingOrders,
    Long confirmedOrders,
    Long shippedOrders,
    Long completedOrders,
    Long cancelledOrders,

    // ========== Inventory Overview ==========
    Long totalProducts,
    Long lowStockProducts,
    Long outOfStockProducts,
    Long totalSuppliers,
    Long pendingPurchaseOrders,
    BigDecimal inventoryValuation,

    // ========== Charts ==========
    List<OrderStatistics.DailyStats> dailyStats,
    List<OrderStatistics.TopProduct> topProducts
) {
}

