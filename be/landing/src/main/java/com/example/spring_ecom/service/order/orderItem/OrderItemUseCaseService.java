package com.example.spring_ecom.service.order.orderItem;

import com.example.spring_ecom.controller.api.order.orderItem.model.PartialCancelRequestItem;
import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.repository.database.order.OrderEntity;
import com.example.spring_ecom.repository.database.order.dao.OrderItemWithProductDao;
import com.example.spring_ecom.repository.database.order.orderItem.OrderItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Internal UseCase for OrderItem operations
 * Only used by OrderCommandService and OrderQueryService
 * Not exposed to controllers directly
 */
@Service
@RequiredArgsConstructor
public class OrderItemUseCaseService implements OrderItemUseCase {
    
    private final OrderItemCommandService commandService;
    private final OrderItemQueryService queryService;
    
    // ========== COMMAND OPERATIONS ==========
    
    @Override
    @Transactional
    public List<OrderItemEntity> createOrderItems(OrderEntity orderEntity, List<CartItem> cartItems) {
        return commandService.createOrderItems(orderEntity, cartItems);
    }
    
    @Override
    @Transactional
    public List<OrderItemEntity> processPartialCancellation(Long orderId, List<PartialCancelRequestItem> cancelItems) {
        return commandService.processPartialCancellation(orderId, cancelItems);
    }
    
    @Override
    @Transactional
    public List<OrderItemEntity> getOrderItemsForRestore(Long orderId) {
        return commandService.getOrderItemsForRestore(orderId);
    }
    
    @Override
    @Transactional
    public void updateSoldCountForOrder(Long orderId) {
        commandService.updateSoldCountForOrder(orderId);
    }
    
    // ========== QUERY OPERATIONS ==========
    
    @Override
    @Transactional(readOnly = true)
    public List<OrderItemWithProductDao> findOrderItemsWithProductByOrderId(Long orderId) {
        return queryService.findOrderItemsWithProductByOrderId(orderId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OrderItemEntity> findByOrderId(Long orderId) {
        return queryService.findByOrderId(orderId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateOrderSubtotal(List<OrderItemEntity> orderItems) {
        return queryService.calculateOrderSubtotal(orderItems);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveItems(List<OrderItemEntity> orderItems) {
        return queryService.hasActiveItems(orderItems);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasCancelledItems(List<OrderItemEntity> orderItems) {
        return queryService.hasCancelledItems(orderItems);
    }
}
