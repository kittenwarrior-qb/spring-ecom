package com.example.spring_ecom.service.order;

import com.example.spring_ecom.controller.api.order.model.OrderResponse;
import com.example.spring_ecom.controller.api.order.orderItem.model.OrderDetailResponse;
import com.example.spring_ecom.controller.api.order.orderItem.model.PartialCancelRequestItem;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.PaymentStatus;
import com.example.spring_ecom.repository.database.order.OrderEntityMapper;
import com.example.spring_ecom.repository.database.order.dao.CreateOrderFromCartDao;
import com.example.spring_ecom.repository.database.order.dao.OrderStatisticsDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderUseCaseService implements OrderUseCase {
    
    private final OrderQueryService queryService;
    private final OrderCommandService commandService;
    private final OrderEntityMapper mapper;
    
    @Override
    @Transactional
    public Order createOrder(Order order) {
        return commandService.create(order)
                .orElseThrow(() -> new RuntimeException("Failed to create order"));
    }
    
    @Override
    @Transactional
    public Order createOrderFromCart(CreateOrderFromCartDao request) {
        return commandService.createFromCartDomain(request)
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
    @Transactional(readOnly = true)
    public Page<OrderResponse> findAllWithUser(Pageable pageable) {
        return queryService.findAllWithUser(pageable);
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
    public void cancelOrder(Long id, Long currentUserId, boolean isAdmin) {
        validateOrderAccess(id, currentUserId, isAdmin);
        commandService.cancel(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetail(Long orderId) {
        return queryService.getOrderDetail(orderId)
                .orElseThrow(() -> new RuntimeException("Order detail not found"));
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderStatisticsDao getOrderStatistics() {
        return queryService.getStatistics();
    }
    
    @Override
    @Transactional
    public Order cancelPartialOrder(Long orderId, List<PartialCancelRequestItem> cancelItems, Long currentUserId, boolean isAdmin) {
        validateOrderAccess(orderId, currentUserId, isAdmin);
        return commandService.cancelPartial(orderId, cancelItems)
                .orElseThrow(() -> new RuntimeException("Failed to cancel partial order"));
    }
    
    // ========== NEW GRPC METHODS ==========
    
    @Override
    @Transactional(readOnly = true)
    public Page<Order> findAllWithFilters(Pageable pageable, String search, String status, 
                                         String paymentStatus, java.time.LocalDate dateFrom, java.time.LocalDate dateTo) {
        // Filter by status if provided
        if (status != null && !status.isBlank()) {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                return queryService.findByStatus(orderStatus, pageable);
            } catch (IllegalArgumentException e) {
                // Invalid status, return all
            }
        }
        return queryService.findAll(pageable);
    }
    
    @Override
    @Transactional
    public Optional<Order> updateStatus(Long orderId, OrderStatus status) {
        return commandService.updateStatus(orderId, status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public com.example.spring_ecom.domain.order.OrderStatistics getOrderStatistics(String period, java.time.LocalDate dateFrom, java.time.LocalDate dateTo) {
        return queryService.getStatisticsWithRange(period, dateFrom, dateTo);
    }
    
    private void validateOrderAccess(Long orderId, Long currentUserId, boolean isAdmin) {
        if (!isAdmin) {
            Order order = findById(orderId)
                    .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Order not found"));
            
            if (!order.userId().equals(currentUserId)) {
                throw new BaseException(ResponseCode.FORBIDDEN, "You can only cancel your own orders");
            }
        }
    }
}
