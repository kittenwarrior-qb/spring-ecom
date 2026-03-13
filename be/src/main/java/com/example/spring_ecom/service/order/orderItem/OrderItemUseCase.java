package com.example.spring_ecom.service.order.orderItem;

import com.example.spring_ecom.controller.api.order.orderItem.model.PartialCancelRequestItem;
import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.repository.database.order.OrderEntity;
import com.example.spring_ecom.repository.database.order.orderItem.OrderItemEntity;
import com.example.spring_ecom.service.order.dao.OrderItemWithProductDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Internal UseCase for OrderItem operations
 * Only used by OrderCommandService and OrderQueryService
 * Not exposed to controllers directly
 */
@Service
@RequiredArgsConstructor
public class OrderItemUseCase {
    
    private final OrderItemCommandService commandService;
    private final OrderItemQueryService queryService;
    
    // ========== COMMAND OPERATIONS ==========
    
    public void createOrderItems(OrderEntity orderEntity, List<CartItem> cartItems) {
        commandService.createOrderItems(orderEntity, cartItems);
    }
    
    public List<OrderItemEntity> processPartialCancellation(Long orderId, List<PartialCancelRequestItem> cancelItems) {
        return commandService.processPartialCancellation(orderId, cancelItems);
    }
    
    public void restoreStockForOrder(Long orderId) {
        commandService.restoreStockForOrder(orderId);
    }
    
    public void updateSoldCountForOrder(Long orderId) {
        commandService.updateSoldCountForOrder(orderId);
    }
    
    // ========== QUERY OPERATIONS ==========
    
    public List<OrderItemWithProductDao> findOrderItemsWithProductByOrderId(Long orderId) {
        return queryService.findOrderItemsWithProductByOrderId(orderId);
    }
    
    public List<OrderItemEntity> findByOrderId(Long orderId) {
        return queryService.findByOrderId(orderId);
    }
    
    public BigDecimal calculateOrderSubtotal(List<OrderItemEntity> orderItems) {
        return queryService.calculateOrderSubtotal(orderItems);
    }
    
    public boolean hasActiveItems(List<OrderItemEntity> orderItems) {
        return queryService.hasActiveItems(orderItems);
    }
    
    public boolean hasCancelledItems(List<OrderItemEntity> orderItems) {
        return queryService.hasCancelledItems(orderItems);
    }
}