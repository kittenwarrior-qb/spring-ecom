package com.example.spring_ecom.service.order.orderItem;

import com.example.spring_ecom.controller.api.order.orderItem.model.PartialCancelRequestItem;
import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.repository.database.order.OrderEntity;
import com.example.spring_ecom.repository.database.order.dao.OrderItemWithProductDao;
import com.example.spring_ecom.repository.database.order.orderItem.OrderItemEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * Internal UseCase for OrderItem operations
 * Only used by OrderCommandService and OrderQueryService
 * Not exposed to controllers directly
 */
public interface OrderItemUseCase {
    
    // ========== COMMAND OPERATIONS ==========
    
    /**
     * Create order items - trả về List<OrderItemEntity> để gửi Kafka event
     */
    List<OrderItemEntity> createOrderItems(OrderEntity orderEntity, List<CartItem> cartItems);
    
    List<OrderItemEntity> processPartialCancellation(Long orderId, List<PartialCancelRequestItem> cancelItems);
    
    /**
     * Lấy order items để gửi Kafka event restore stock
     */
    List<OrderItemEntity> getOrderItemsForRestore(Long orderId);
    
    void updateSoldCountForOrder(Long orderId);
    
    // ========== QUERY OPERATIONS ==========
    
    List<OrderItemWithProductDao> findOrderItemsWithProductByOrderId(Long orderId);
    
    List<OrderItemEntity> findByOrderId(Long orderId);
    
    BigDecimal calculateOrderSubtotal(List<OrderItemEntity> orderItems);
    
    boolean hasActiveItems(List<OrderItemEntity> orderItems);
    
    boolean hasCancelledItems(List<OrderItemEntity> orderItems);
}