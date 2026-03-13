package com.example.spring_ecom.service.order.detail;

import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.repository.database.order.OrderEntity;
import com.example.spring_ecom.repository.database.order.OrderRepository;
import com.example.spring_ecom.repository.database.order.dao.OrderStatisticsDao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderStatisticsService {
    
    private final OrderRepository orderRepository;
    
    public OrderStatisticsDao getStatistics() {
        List<OrderEntity> allOrders = orderRepository.findAll();
        
        long totalOrders = allOrders.size();
        long pendingOrders = countByStatus(allOrders, OrderStatus.PENDING);
        long confirmedOrders = countByStatus(allOrders, OrderStatus.CONFIRMED);
        long shippedOrders = countByStatus(allOrders, OrderStatus.SHIPPED);
        long deliveredOrders = countByStatus(allOrders, OrderStatus.DELIVERED);
        long cancelledOrders = countByStatus(allOrders, OrderStatus.CANCELLED);
        long partiallyCancelledOrders = countByStatus(allOrders, OrderStatus.PARTIALLY_CANCELLED);
        
        BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(OrderEntity::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        BigDecimal todayRevenue = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .filter(o -> o.getCreatedAt().isAfter(startOfDay) && o.getCreatedAt().isBefore(endOfDay))
                .map(OrderEntity::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return new OrderStatisticsDao(
                totalOrders,
                pendingOrders,
                confirmedOrders,
                shippedOrders,
                deliveredOrders,
                cancelledOrders,
                partiallyCancelledOrders,
                totalRevenue,
                todayRevenue
        );
    }
    
    private long countByStatus(List<OrderEntity> orders, OrderStatus status) {
        return orders.stream()
                .filter(o -> o.getStatus() == status)
                .count();
    }
}
