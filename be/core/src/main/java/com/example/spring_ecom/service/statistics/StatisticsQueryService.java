package com.example.spring_ecom.service.statistics;

import com.example.spring_ecom.domain.statistics.DashboardSummary;
import com.example.spring_ecom.domain.statistics.RevenueByCategoryItem;
import com.example.spring_ecom.domain.statistics.RevenueByPeriod;
import com.example.spring_ecom.domain.statistics.TopSellingProduct;
import com.example.spring_ecom.repository.database.order.dao.OrderStatisticsDao;
import com.example.spring_ecom.service.inventory.InventoryUseCase;
import com.example.spring_ecom.service.order.OrderUseCase;
import com.example.spring_ecom.service.product.ProductUseCase;
import com.example.spring_ecom.service.supplier.SupplierUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsQueryService {

    private final OrderUseCase orderUseCase;
    private final ProductUseCase productUseCase;
    private final SupplierUseCase supplierUseCase;
    private final InventoryUseCase inventoryUseCase;

    // ========== Dashboard Summary ==========

    @Transactional(readOnly = true)
    public DashboardSummary getDashboardSummary(String period, LocalDate dateFrom, LocalDate dateTo) {
        LocalDate[] range = resolveRange(period, dateFrom, dateTo);
        LocalDateTime from = range[0].atStartOfDay();
        LocalDateTime to = range[1].atTime(LocalTime.MAX);

        OrderStatisticsDao stats = orderUseCase.getOrderStatisticsInRange(from, to);

        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        BigDecimal todayRevenue = orderUseCase.getTodayRevenue(startOfDay, endOfDay);

        Object[] rcp = orderUseCase.getRevenueCostProfit(from, to);
        // JPA native query có thể wrap kết quả: Object[1] chứa Object[3]
        // Cần unwrap để lấy đúng [revenue, cost, profit]
        if (rcp != null && rcp.length == 1 && rcp[0] instanceof Object[]) {
            rcp = (Object[]) rcp[0];
        }
        BigDecimal revenue = toBigDecimal(rcp, 0);
        BigDecimal cost = toBigDecimal(rcp, 1);
        BigDecimal profit = toBigDecimal(rcp, 2);
        double profitMargin = revenue.doubleValue() > 0
                ? profit.doubleValue() / revenue.doubleValue() * 100.0
                : 0.0;

        BigDecimal avgOrderValue = orderUseCase.getAverageOrderValue(from, to);

        Long totalProducts = productUseCase.countActiveProducts();
        Long lowStockProducts = productUseCase.countLowStockProducts();
        Long outOfStockProducts = productUseCase.countOutOfStockProducts();
        Long totalSuppliers = supplierUseCase.countActiveSuppliers();
        Long pendingPOs = inventoryUseCase.countPendingPurchaseOrders();

        BigDecimal inventoryValuation = inventoryUseCase.getTotalInventoryValuation();

        return new DashboardSummary(
                stats.getTotalOrders(),
                revenue,
                cost,
                profit,
                Math.round(profitMargin * 100.0) / 100.0,
                todayRevenue,
                avgOrderValue != null ? avgOrderValue : BigDecimal.ZERO,
                stats.getPendingOrders(),
                stats.getConfirmedOrders(),
                stats.getShippedOrders(),
                stats.getDeliveredOrders(),
                stats.getCancelledOrders(),
                stats.getPartiallyCancelledOrders(),
                totalProducts,
                lowStockProducts,
                outOfStockProducts,
                totalSuppliers,
                pendingPOs,
                inventoryValuation != null ? inventoryValuation : BigDecimal.ZERO
        );
    }

    // ========== Revenue by Period ==========

    @Transactional(readOnly = true)
    public List<RevenueByPeriod> getRevenueByPeriod(LocalDate dateFrom, LocalDate dateTo, String granularity) {
        LocalDateTime from = dateFrom.atStartOfDay();
        LocalDateTime to = dateTo.atTime(LocalTime.MAX);

        List<Object[]> rows = orderUseCase.getProfitBreakdown(from, to, granularity);

        return rows.stream()
                .map(row -> new RevenueByPeriod(
                        toLocalDate(row[0]),
                        ((Number) row[1]).longValue(),
                        toBigDecimal(row, 2),
                        toBigDecimal(row, 3),
                        toBigDecimal(row, 4)
                ))
                .toList();
    }

    // ========== Profit by Period (same data, different semantic) ==========

    @Transactional(readOnly = true)
    public List<RevenueByPeriod> getProfitByPeriod(LocalDate dateFrom, LocalDate dateTo, String granularity) {
        return getRevenueByPeriod(dateFrom, dateTo, granularity);
    }

    // ========== Top Selling Products ==========

    @Transactional(readOnly = true)
    public List<TopSellingProduct> getTopSellingProducts(String period, LocalDate dateFrom, LocalDate dateTo, int limit) {
        LocalDate[] range = resolveRange(period, dateFrom, dateTo);
        LocalDateTime from = range[0].atStartOfDay();
        LocalDateTime to = range[1].atTime(LocalTime.MAX);

        List<Object[]> rows = orderUseCase.getTopSellingProducts(from, to, limit);
        return rows.stream()
                .map(row -> new TopSellingProduct(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        ((Number) row[2]).longValue(),
                        toBigDecimal(row, 3),
                        BigDecimal.ZERO // profit per product can be extended
                ))
                .toList();
    }

    // ========== Revenue by Category ==========

    @Transactional(readOnly = true)
    public List<RevenueByCategoryItem> getRevenueByCategory(String period, LocalDate dateFrom, LocalDate dateTo) {
        LocalDate[] range = resolveRange(period, dateFrom, dateTo);
        LocalDateTime from = range[0].atStartOfDay();
        LocalDateTime to = range[1].atTime(LocalTime.MAX);

        List<Object[]> rows = orderUseCase.getRevenueByCategoryInRange(from, to);
        return rows.stream()
                .map(row -> new RevenueByCategoryItem(
                        row[0] != null ? ((Number) row[0]).longValue() : null,
                        row[1] != null ? (String) row[1] : "Uncategorized",
                        ((Number) row[2]).longValue(),
                        toBigDecimal(row, 3),
                        toBigDecimal(row, 4),
                        toBigDecimal(row, 5)
                ))
                .toList();
    }

    // ========== Inventory Valuation ==========

    @Transactional(readOnly = true)
    public BigDecimal getInventoryValuation() {
        BigDecimal val = inventoryUseCase.getTotalInventoryValuation();
        return val != null ? val : BigDecimal.ZERO;
    }

    // ========== Helper Methods ==========

    private LocalDate[] resolveRange(String period, LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom != null && dateTo != null) {
            return new LocalDate[]{dateFrom, dateTo};
        }
        LocalDate end = LocalDate.now();
        LocalDate start = switch (period != null ? period.toLowerCase() : "monthly") {
            case "daily" -> end;
            case "weekly" -> end.minusWeeks(1);
            case "yearly" -> end.minusYears(1);
            default -> end.minusMonths(1);
        };
        return new LocalDate[]{start, end};
    }

    private BigDecimal toBigDecimal(Object[] row, int index) {
        if (row == null || row.length <= index) return BigDecimal.ZERO;

        Object value = unwrapProjectionValue(row[index]);
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd) return bd;
        if (value instanceof Number n) return BigDecimal.valueOf(n.doubleValue());

        log.warn("[STATISTICS] Unexpected numeric projection type at index {}: {}", index, value.getClass().getName());
        return BigDecimal.ZERO;
    }

    private Object unwrapProjectionValue(Object value) {
        Object current = value;

        // Some native-query projections can be wrapped as nested Object[] tuples.
        while (current instanceof Object[] arr) {
            if (arr.length == 0) {
                return null;
            }
            current = arr[0];
        }

        return current;
    }

    private LocalDate toLocalDate(Object obj) {
        if (obj instanceof java.sql.Date sqlDate) return sqlDate.toLocalDate();
        if (obj instanceof java.sql.Timestamp ts) return ts.toLocalDateTime().toLocalDate();
        if (obj instanceof LocalDate ld) return ld;
        return LocalDate.parse(obj.toString());
    }
}

