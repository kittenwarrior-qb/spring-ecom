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
        return orderRepository.findAll(pageable)
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