package com.example.spring_ecom.service.order.detail;

import com.example.spring_ecom.domain.order.OrderStatistics;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.repository.database.order.OrderEntity;
import com.example.spring_ecom.repository.database.order.OrderRepository;
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
    
    public OrderStatistics getStatistics() {
        List<OrderEntity> allOrders = orderRepository.findAll();
        
        long totalOrders = allOrders.size();
        long pendingOrders = countByStatus(allOrders, OrderStatus.PENDING);
        long processingOrders = countByStatus(allOrders, OrderStatus.PROCESSING);
        long shippedOrders = countByStatus(allOrders, OrderStatus.SHIPPED);
        long deliveredOrders = countByStatus(allOrders, OrderStatus.DELIVERED);
        long cancelledOrders = countByStatus(allOrders, OrderStatus.CANCELLED);
        
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
        
        return new OrderStatistics(
                totalOrders,
                pendingOrders,
                processingOrders,
                shippedOrders,
                deliveredOrders,
                cancelledOrders,
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
