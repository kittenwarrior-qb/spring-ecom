package com.example.spring_ecom.controller.api.admin.statistics;

import com.example.spring_ecom.controller.api.admin.statistics.model.*;
import com.example.spring_ecom.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/v1/api/admin/statistics")
@Tag(name = "Admin Statistics", description = "Profit calculation & analytics dashboard")
public interface AdminStatisticsAPI {

    @Operation(summary = "Get dashboard summary", description = "Aggregated statistics: orders, revenue, cost, profit, inventory")
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('STATISTICS_VIEW')")
    ResponseEntity<ApiResponse<StatisticsDashboardResponse>> getDashboard(
            @Parameter(description = "Period: daily, weekly, monthly, yearly") @RequestParam(defaultValue = "monthly") String period,
            @Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam(required = false) String startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam(required = false) String endDate);

    @Operation(summary = "Get revenue by period", description = "Revenue, cost, profit breakdown by day/week/month")
    @GetMapping("/revenue")
    @PreAuthorize("hasAuthority('STATISTICS_VIEW')")
    ResponseEntity<ApiResponse<List<RevenueByPeriodResponse>>> getRevenue(
            @Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam String from,
            @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam String to,
            @Parameter(description = "Granularity: daily, weekly, monthly") @RequestParam(defaultValue = "daily") String granularity);

    @Operation(summary = "Get profit by period", description = "Revenue, COGS, gross profit breakdown by day/week/month")
    @GetMapping("/profit")
    @PreAuthorize("hasAuthority('STATISTICS_VIEW')")
    ResponseEntity<ApiResponse<List<RevenueByPeriodResponse>>> getProfit(
            @Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam String from,
            @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam String to,
            @Parameter(description = "Granularity: daily, weekly, monthly") @RequestParam(defaultValue = "daily") String granularity);

    @Operation(summary = "Get top selling products", description = "Top products by quantity sold")
    @GetMapping("/top-products")
    @PreAuthorize("hasAuthority('STATISTICS_VIEW')")
    ResponseEntity<ApiResponse<List<TopSellingProductResponse>>> getTopProducts(
            @Parameter(description = "Period: daily, weekly, monthly, yearly") @RequestParam(defaultValue = "monthly") String period,
            @Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam(required = false) String startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam(required = false) String endDate,
            @Parameter(description = "Number of results") @RequestParam(defaultValue = "10") int limit);

    @Operation(summary = "Get revenue by category", description = "Revenue, cost, profit breakdown by product category")
    @GetMapping("/revenue-by-category")
    @PreAuthorize("hasAuthority('STATISTICS_VIEW')")
    ResponseEntity<ApiResponse<List<RevenueByCategoryResponse>>> getRevenueByCategory(
            @Parameter(description = "Period: daily, weekly, monthly, yearly") @RequestParam(defaultValue = "monthly") String period,
            @Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam(required = false) String startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam(required = false) String endDate);

    @Operation(summary = "Get inventory valuation", description = "Total stock value based on cost batches")
    @GetMapping("/inventory-valuation")
    @PreAuthorize("hasAuthority('STATISTICS_VIEW')")
    ResponseEntity<ApiResponse<InventoryValuationResponse>> getInventoryValuation();
}

