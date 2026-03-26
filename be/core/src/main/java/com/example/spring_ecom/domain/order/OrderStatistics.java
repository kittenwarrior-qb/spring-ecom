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
    private Long pendingOrders;
    private Long completedOrders;
    private Long cancelledOrders;
    private List<DailyStats> dailyStats;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyStats {
        private LocalDate date;
        private Long orders;
        private Double revenue;
    }
}