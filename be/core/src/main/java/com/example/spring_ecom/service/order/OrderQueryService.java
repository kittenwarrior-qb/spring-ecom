package com.example.spring_ecom.service.order;

import com.example.spring_ecom.controller.api.order.model.OrderResponse;
import com.example.spring_ecom.controller.api.order.model.OrderResponseMapper;
import com.example.spring_ecom.controller.api.order.orderItem.model.OrderDetailResponse;
import com.example.spring_ecom.controller.api.order.orderItem.model.OrderDetailResponseMapper;
import com.example.spring_ecom.controller.api.order.orderItem.model.OrderItemResponse;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.OrderItem.OrderItemWithProductDto;
import com.example.spring_ecom.repository.database.order.OrderEntity;
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
}