package com.example.spring_ecom.service.statistics;

import com.example.spring_ecom.domain.statistics.DashboardSummary;
import com.example.spring_ecom.domain.statistics.RevenueByCategoryItem;
import com.example.spring_ecom.domain.statistics.RevenueByPeriod;
import com.example.spring_ecom.domain.statistics.TopSellingProduct;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface StatisticsUseCase {

    // ========== Dashboard ==========

    DashboardSummary getDashboardSummary(String period, LocalDate dateFrom, LocalDate dateTo);

    // ========== Revenue & Profit ==========

    List<RevenueByPeriod> getRevenueByPeriod(LocalDate dateFrom, LocalDate dateTo, String granularity);

    List<RevenueByPeriod> getProfitByPeriod(LocalDate dateFrom, LocalDate dateTo, String granularity);

    // ========== Top Products ==========

    List<TopSellingProduct> getTopSellingProducts(String period, LocalDate dateFrom, LocalDate dateTo, int limit);

    // ========== Revenue by Category ==========

    List<RevenueByCategoryItem> getRevenueByCategory(String period, LocalDate dateFrom, LocalDate dateTo);

    // ========== Inventory Valuation ==========

    BigDecimal getInventoryValuation();
}

