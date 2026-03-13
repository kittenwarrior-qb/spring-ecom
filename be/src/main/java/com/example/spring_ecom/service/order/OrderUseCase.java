package com.example.spring_ecom.service.order;

import com.example.spring_ecom.controller.api.order.orderItem.model.OrderDetailResponse;
import com.example.spring_ecom.controller.api.order.orderItem.model.PartialCancelRequestItem;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.PaymentStatus;
import com.example.spring_ecom.repository.database.order.dao.OrderStatisticsDao;

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
    
    OrderStatisticsDao getOrderStatistics();
    
    Order cancelPartialOrder(Long orderId, List<PartialCancelRequestItem> cancelItems);
}
