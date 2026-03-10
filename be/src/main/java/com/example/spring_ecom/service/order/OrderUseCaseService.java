package com.example.spring_ecom.service.order;

import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.PaymentStatus;
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
    
    @Override
    @Transactional
    public Order createOrder(Order order) {
        return commandService.createOrder(order);
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
        return commandService.updateOrderStatus(id, status);
    }
    
    @Override
    @Transactional
    public Order updatePaymentStatus(Long id, PaymentStatus paymentStatus) {
        return commandService.updatePaymentStatus(id, paymentStatus);
    }
    
    @Override
    @Transactional
    public void cancelOrder(Long id) {
        commandService.cancelOrder(id);
    }
}
