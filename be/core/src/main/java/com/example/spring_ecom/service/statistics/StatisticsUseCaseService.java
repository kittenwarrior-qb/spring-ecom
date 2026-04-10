package com.example.spring_ecom.service.statistics;

import com.example.spring_ecom.domain.statistics.DashboardSummary;
import com.example.spring_ecom.domain.statistics.RevenueByCategoryItem;
import com.example.spring_ecom.domain.statistics.RevenueByPeriod;
import com.example.spring_ecom.domain.statistics.TopSellingProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsUseCaseService implements StatisticsUseCase {

    private final StatisticsQueryService queryService;
    private final StatisticsCommandService commandService;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummary getDashboardSummary(String period, LocalDate dateFrom, LocalDate dateTo) {
        return queryService.getDashboardSummary(period, dateFrom, dateTo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevenueByPeriod> getRevenueByPeriod(LocalDate dateFrom, LocalDate dateTo, String granularity) {
        return queryService.getRevenueByPeriod(dateFrom, dateTo, granularity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevenueByPeriod> getProfitByPeriod(LocalDate dateFrom, LocalDate dateTo, String granularity) {
        return queryService.getProfitByPeriod(dateFrom, dateTo, granularity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopSellingProduct> getTopSellingProducts(String period, LocalDate dateFrom, LocalDate dateTo, int limit) {
        return queryService.getTopSellingProducts(period, dateFrom, dateTo, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevenueByCategoryItem> getRevenueByCategory(String period, LocalDate dateFrom, LocalDate dateTo) {
        return queryService.getRevenueByCategory(period, dateFrom, dateTo);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getInventoryValuation() {
        return queryService.getInventoryValuation();
    }
}

