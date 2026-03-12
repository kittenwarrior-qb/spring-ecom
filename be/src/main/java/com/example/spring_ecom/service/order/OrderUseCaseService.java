package com.example.spring_ecom.service.order;

import com.example.spring_ecom.controller.api.order.model.OrderDetailResponse;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderStatistics;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.PaymentStatus;
import com.example.spring_ecom.service.order.detail.OrderDetailService;
import com.example.spring_ecom.service.order.detail.OrderStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderUseCaseService implements OrderUseCase {
    
    private final OrderQueryService queryService;
    private final OrderCommandService commandService;
    private final OrderDetailService orderDetailService;
    private final OrderStatisticsService orderStatisticsService;
    
    @Override
    @Transactional
    public Order createOrder(Order order) {
        return commandService.create(order)
                .orElseThrow(() -> new RuntimeException("Failed to create order"));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(Long id) {
        return queryService.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return queryService.findByOrderNumber(orderNumber);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Order> findByUserId(Long userId, Pageable pageable) {
        return queryService.findByUserId(userId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Order> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable) {
        return queryService.findByUserIdAndStatus(userId, status, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Order> findAll(Pageable pageable) {
        return queryService.findAll(pageable);
    }
    
    @Override
    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {
        return commandService.updateStatus(id, status)
                .orElseThrow(() -> new RuntimeException("Failed to update order status"));
    }
    
    @Override
    @Transactional
    public Order updatePaymentStatus(Long id, PaymentStatus paymentStatus) {
        return commandService.updatePaymentStatus(id, paymentStatus)
                .orElseThrow(() -> new RuntimeException("Failed to update payment status"));
    }
    
    @Override
    @Transactional
    public void cancelOrder(Long id) {
        commandService.cancel(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetail(Long orderId) {
        return orderDetailService.getOrderDetail(orderId)
                .orElseThrow(() -> new RuntimeException("Order detail not found"));
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderStatistics getOrderStatistics() {
        return orderStatisticsService.getStatistics();
    }
}
