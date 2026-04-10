package com.example.spring_ecom.controller.api.admin.statistics;

import com.example.spring_ecom.controller.api.admin.statistics.model.*;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.domain.statistics.DashboardSummary;
import com.example.spring_ecom.domain.statistics.RevenueByCategoryItem;
import com.example.spring_ecom.domain.statistics.RevenueByPeriod;
import com.example.spring_ecom.domain.statistics.TopSellingProduct;
import com.example.spring_ecom.service.statistics.StatisticsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminStatisticsController implements AdminStatisticsAPI {

    private final StatisticsUseCase statisticsUseCase;
    private final StatisticsResponseMapper responseMapper;

    @Override
    public ResponseEntity<ApiResponse<StatisticsDashboardResponse>> getDashboard(String period, String startDate, String endDate) {
        log.info("Admin getting statistics dashboard: period={}", period);
        LocalDate from = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate to = endDate != null ? LocalDate.parse(endDate) : null;

        DashboardSummary summary = statisticsUseCase.getDashboardSummary(period, from, to);
        StatisticsDashboardResponse response = responseMapper.toResponse(summary);
        return ResponseEntity.ok(ApiResponse.Success.of(response));
    }

    @Override
    public ResponseEntity<ApiResponse<List<RevenueByPeriodResponse>>> getRevenue(String from, String to, String granularity) {
        log.info("Admin getting revenue: from={}, to={}, granularity={}", from, to, granularity);
        LocalDate dateFrom = LocalDate.parse(from);
        LocalDate dateTo = LocalDate.parse(to);

        List<RevenueByPeriod> data = statisticsUseCase.getRevenueByPeriod(dateFrom, dateTo, granularity);
        List<RevenueByPeriodResponse> response = responseMapper.toRevenueByPeriodResponseList(data);
        return ResponseEntity.ok(ApiResponse.Success.of(response));
    }

    @Override
    public ResponseEntity<ApiResponse<List<RevenueByPeriodResponse>>> getProfit(String from, String to, String granularity) {
        log.info("Admin getting profit: from={}, to={}, granularity={}", from, to, granularity);
        LocalDate dateFrom = LocalDate.parse(from);
        LocalDate dateTo = LocalDate.parse(to);

        List<RevenueByPeriod> data = statisticsUseCase.getProfitByPeriod(dateFrom, dateTo, granularity);
        List<RevenueByPeriodResponse> response = responseMapper.toRevenueByPeriodResponseList(data);
        return ResponseEntity.ok(ApiResponse.Success.of(response));
    }

    @Override
    public ResponseEntity<ApiResponse<List<TopSellingProductResponse>>> getTopProducts(String period, String startDate, String endDate, int limit) {
        log.info("Admin getting top products: period={}, limit={}", period, limit);
        LocalDate from = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate to = endDate != null ? LocalDate.parse(endDate) : null;

        List<TopSellingProduct> data = statisticsUseCase.getTopSellingProducts(period, from, to, limit);
        List<TopSellingProductResponse> response = responseMapper.toTopSellingProductResponseList(data);
        return ResponseEntity.ok(ApiResponse.Success.of(response));
    }

    @Override
    public ResponseEntity<ApiResponse<List<RevenueByCategoryResponse>>> getRevenueByCategory(String period, String startDate, String endDate) {
        log.info("Admin getting revenue by category: period={}", period);
        LocalDate from = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate to = endDate != null ? LocalDate.parse(endDate) : null;

        List<RevenueByCategoryItem> data = statisticsUseCase.getRevenueByCategory(period, from, to);
        List<RevenueByCategoryResponse> response = responseMapper.toRevenueByCategoryResponseList(data);
        return ResponseEntity.ok(ApiResponse.Success.of(response));
    }

    @Override
    public ResponseEntity<ApiResponse<InventoryValuationResponse>> getInventoryValuation() {
        log.info("Admin getting inventory valuation");
        BigDecimal valuation = statisticsUseCase.getInventoryValuation();
        InventoryValuationResponse response = new InventoryValuationResponse(valuation);
        return ResponseEntity.ok(ApiResponse.Success.of(response));
    }
}

