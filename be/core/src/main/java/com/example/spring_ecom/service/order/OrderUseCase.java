package com.example.spring_ecom.service.order;

import com.example.spring_ecom.controller.api.order.model.OrderResponse;
import com.example.spring_ecom.controller.api.order.orderItem.model.OrderDetailResponse;
import com.example.spring_ecom.controller.api.order.orderItem.model.PartialCancelRequestItem;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.PaymentStatus;
import com.example.spring_ecom.domain.order.OrderStatistics;
import com.example.spring_ecom.repository.database.order.dao.CreateOrderFromCartDao;
import com.example.spring_ecom.repository.database.order.dao.OrderStatisticsDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderUseCase {
    
    // ========== COMMAND OPERATIONS ==========
    
    Order createOrder(Order order);
    
    Order createOrderFromCart(CreateOrderFromCartDao request);
    
    Order updateOrderStatus(Long id, OrderStatus status);
    
    void cancelOrder(Long id, Long currentUserId, boolean isAdmin);
    
    Order cancelPartialOrder(Long orderId, List<PartialCancelRequestItem> cancelItems, Long currentUserId, boolean isAdmin);
    
    Order updatePaymentStatus(Long id, PaymentStatus paymentStatus);
    
    // ========== QUERY OPERATIONS ==========
    
    Optional<Order> findById(Long id);
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    Page<Order> findByUserId(Long userId, Pageable pageable);
    
    Page<Order> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);
    
    Page<Order> findAll(Pageable pageable);
    
    Page<OrderResponse> findAllWithUser(Pageable pageable);
    
    OrderDetailResponse getOrderDetail(Long orderId);
    
    OrderStatisticsDao getOrderStatistics();
    
    // ========== NEW GRPC METHODS ==========
    
    Page<Order> findAllWithFilters(Pageable pageable, String search, String status, 
                                  String paymentStatus, LocalDate dateFrom, LocalDate dateTo);
    
    Optional<Order> updateStatus(Long orderId, OrderStatus status);
    
    OrderStatistics getOrderStatistics(String period, LocalDate dateFrom, LocalDate dateTo);
}