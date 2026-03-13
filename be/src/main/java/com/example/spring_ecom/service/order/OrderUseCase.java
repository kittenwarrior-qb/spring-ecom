package com.example.spring_ecom.service.order;

import com.example.spring_ecom.controller.api.order.dao.OrderStatisticsDto;
import com.example.spring_ecom.controller.api.order.model.OrderDetailResponse;
import com.example.spring_ecom.controller.api.order.model.PartialCancelRequest.PartialCancelItem;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderUseCase {
    
    Order createOrder(Order order);
    
    Optional<Order> findById(Long id);
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    Page<Order> findByUserId(Long userId, Pageable pageable);
    
    Page<Order> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);
    
    Page<Order> findAll(Pageable pageable);
    
    Order updateOrderStatus(Long id, OrderStatus status);
    
    Order updatePaymentStatus(Long id, PaymentStatus paymentStatus);
    
    void cancelOrder(Long id);
    
    OrderDetailResponse getOrderDetail(Long orderId);
    
    OrderStatisticsDto getOrderStatistics();
    
    Order cancelPartialOrder(Long orderId, List<PartialCancelItem> cancelItems);
}
