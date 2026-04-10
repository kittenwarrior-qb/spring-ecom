package com.example.spring_ecom.domain.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatistics {
    private Long totalOrders;
    private Double totalRevenue;
    private Double totalCost;
    private Double totalProfit;
    private Double profitMargin;
    private Double todayRevenue;
    private Long pendingOrders;
    private Long confirmedOrders;
    private Long shippedOrders;
    private Long completedOrders;
    private Long cancelledOrders;
    private Long partiallyCancelledOrders;
    private List<DailyStats> dailyStats;
    private List<TopProduct> topProducts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyStats {
        private LocalDate date;
        private Long orders;
        private Double revenue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProduct {
        private Long productId;
        private String productTitle;
        private Long totalSold;
        private Double totalRevenue;
    }
}