package com.example.spring_ecom.service.order;

import com.example.spring_ecom.controller.api.order.model.OrderResponse;
import com.example.spring_ecom.controller.api.order.model.OrderResponseMapper;
import com.example.spring_ecom.controller.api.order.orderItem.model.OrderDetailResponse;
import com.example.spring_ecom.controller.api.order.orderItem.model.OrderDetailResponseMapper;
import com.example.spring_ecom.controller.api.order.orderItem.model.OrderItemResponse;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderStatistics;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.OrderItem.OrderItemWithProductDto;
import com.example.spring_ecom.repository.database.order.OrderRepository;
import com.example.spring_ecom.repository.database.order.dao.OrderItemWithProductDao;
import com.example.spring_ecom.repository.database.order.dao.OrderStatisticsDao;
import com.example.spring_ecom.repository.database.order.dao.OrderWithUserDao;
import com.example.spring_ecom.repository.database.order.OrderEntityMapper;
import com.example.spring_ecom.service.order.orderItem.OrderItemUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderQueryService {
    
    private final OrderRepository orderRepository;
    private final OrderItemUseCase orderItemUseCase;
    private final OrderDetailResponseMapper detailMapper;
    private final OrderResponseMapper orderResponseMapper;
    private final OrderEntityMapper orderEntityMapper;
    
    // ========== MAIN QUERY METHODS ==========
    
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id)
                .map(orderEntityMapper::toDomain);
    }
    
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(orderEntityMapper::toDomain);
    }
    
    public Page<Order> findByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(orderEntityMapper::toDomain);
    }
    
    public Page<Order> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable) {
        return orderRepository.findByUserIdAndStatus(userId, status, pageable)
                .map(orderEntityMapper::toDomain);
    }
    
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAllOrderByCreatedAtDesc(pageable)
                .map(orderEntityMapper::toDomain);
    }
    
    public Page<Order> findByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable)
                .map(orderEntityMapper::toDomain);
    }
    
    public Page<OrderResponse> findAllWithUser(Pageable pageable) {
        return orderRepository.findAllOrdersWithUser(pageable)
                .map(orderResponseMapper::toResponse);
    }
    
    public Optional<OrderDetailResponse> getOrderDetail(Long orderId) {
        OrderWithUserDao orderDao = findOrderWithUserById(orderId);
        List<OrderItemWithProductDao> orderItemDaos = orderItemUseCase.findOrderItemsWithProductByOrderId(orderId);
        
        List<OrderItemWithProductDto> orderItems = detailMapper.toDtoList(orderItemDaos);
        List<OrderItemResponse> items = detailMapper.toResponseList(orderItems);
        
        OrderDetailResponse response = detailMapper.toDetailResponse(orderDao, items);
        return Optional.of(response);
    }
    
    // ========== HELPER METHODS ==========
    
    private OrderWithUserDao findOrderWithUserById(Long orderId) {
        return orderRepository.findOrderWithUserById(orderId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Order not found"));
    }
    
    // ========== STATISTICS METHODS ==========
    
    /**
     * Optimized: Uses aggregation queries instead of loading all orders into memory
     * Before: Load all orders into memory (O(n) memory)
     * After: 2 queries (1 for statistics, 1 for today's revenue)
     */
    public OrderStatisticsDao getStatistics() {
        OrderStatisticsDao stats = orderRepository.getOrderStatistics();
        
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        BigDecimal todayRevenue = orderRepository.getTodayRevenue(startOfDay, endOfDay);
        
        // Return new implementation with today's revenue
        final Long totalOrders = stats.getTotalOrders();
        final Long pendingOrders = stats.getPendingOrders();
        final Long confirmedOrders = stats.getConfirmedOrders();
        final Long shippedOrders = stats.getShippedOrders();
        final Long deliveredOrders = stats.getDeliveredOrders();
        final Long cancelledOrders = stats.getCancelledOrders();
        final Long partiallyCancelledOrders = stats.getPartiallyCancelledOrders();
        final BigDecimal totalRevenue = stats.getTotalRevenue();
        
        return new OrderStatisticsDao() {
            @Override
            public Long getTotalOrders() { return totalOrders; }
            @Override
            public Long getPendingOrders() { return pendingOrders; }
            @Override
            public Long getConfirmedOrders() { return confirmedOrders; }
            @Override
            public Long getShippedOrders() { return shippedOrders; }
            @Override
            public Long getDeliveredOrders() { return deliveredOrders; }
            @Override
            public Long getCancelledOrders() { return cancelledOrders; }
            @Override
            public Long getPartiallyCancelledOrders() { return partiallyCancelledOrders; }
            @Override
            public BigDecimal getTotalRevenue() { return totalRevenue; }
            @Override
            public BigDecimal getTodayRevenue() { return todayRevenue; }
        };
    }

    /**
     * Full statistics with date range, daily breakdown, profit, and top products
     */
    public OrderStatistics getStatisticsWithRange(String period, LocalDate dateFrom, LocalDate dateTo) {
        // Default range based on period
        if (dateFrom == null || dateTo == null) {
            dateTo = LocalDate.now();
            dateFrom = switch (period != null ? period.toLowerCase() : "monthly") {
                case "daily" -> dateTo;
                case "weekly" -> dateTo.minusWeeks(1);
                case "yearly" -> dateTo.minusYears(1);
                default -> dateTo.minusMonths(1); // monthly
            };
        }

        LocalDateTime from = dateFrom.atStartOfDay();
        LocalDateTime to = dateTo.atTime(LocalTime.MAX);

        // 1) Order counts by status
        OrderStatisticsDao stats = orderRepository.getOrderStatisticsInRange(from, to);

        // 2) Today's revenue
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        BigDecimal todayRevenue = orderRepository.getTodayRevenue(startOfDay, endOfDay);

        // 3) Revenue / cost / profit
        Object[] rcp = orderRepository.getRevenueCostProfit(from, to);
        BigDecimal revenue = toBigDecimal(rcp, 0);
        BigDecimal cost = toBigDecimal(rcp, 1);
        BigDecimal profit = toBigDecimal(rcp, 2);
        double profitMargin = revenue.doubleValue() > 0
                ? profit.doubleValue() / revenue.doubleValue() * 100.0
                : 0.0;

        // 4) Daily breakdown
        List<Object[]> dailyRows = orderRepository.getDailyStatistics(from, to);
        List<OrderStatistics.DailyStats> dailyStats = dailyRows.stream()
                .map(row -> OrderStatistics.DailyStats.builder()
                        .date(((java.sql.Date) row[0]).toLocalDate())
                        .orders(((Number) row[1]).longValue())
                        .revenue(((Number) row[2]).doubleValue())
                        .build())
                .toList();

        // 5) Top selling products
        List<Object[]> topRows = orderRepository.getTopSellingProducts(from, to, 10);
        List<OrderStatistics.TopProduct> topProducts = topRows.stream()
                .map(row -> OrderStatistics.TopProduct.builder()
                        .productId(((Number) row[0]).longValue())
                        .productTitle((String) row[1])
                        .totalSold(((Number) row[2]).longValue())
                        .totalRevenue(((Number) row[3]).doubleValue())
                        .build())
                .toList();

        return OrderStatistics.builder()
                .totalOrders(stats.getTotalOrders())
                .totalRevenue(stats.getTotalRevenue().doubleValue())
                .totalCost(cost.doubleValue())
                .totalProfit(profit.doubleValue())
                .profitMargin(Math.round(profitMargin * 100.0) / 100.0)
                .todayRevenue(todayRevenue.doubleValue())
                .pendingOrders(stats.getPendingOrders())
                .confirmedOrders(stats.getConfirmedOrders())
                .shippedOrders(stats.getShippedOrders())
                .completedOrders(stats.getDeliveredOrders())
                .cancelledOrders(stats.getCancelledOrders())
                .partiallyCancelledOrders(stats.getPartiallyCancelledOrders())
                .dailyStats(dailyStats)
                .topProducts(topProducts)
                .build();
    }

    private BigDecimal toBigDecimal(Object[] row, int index) {
        if (row == null || row.length <= index) return BigDecimal.ZERO;

        Object value = unwrapProjectionValue(row[index]);
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd) return bd;
        if (value instanceof Number n) return BigDecimal.valueOf(n.doubleValue());

        return BigDecimal.ZERO;
    }

    private Object unwrapProjectionValue(Object value) {
        Object current = value;
        while (current instanceof Object[] arr) {
            if (arr.length == 0) {
                return null;
            }
            current = arr[0];
        }
        return current;
    }
}