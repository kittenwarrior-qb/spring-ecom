package com.example.spring_ecom.service.order.orderItem;

import com.example.spring_ecom.repository.database.order.dao.OrderItemWithProductDao;
import com.example.spring_ecom.repository.database.order.orderItem.OrderItemEntity;
import com.example.spring_ecom.repository.database.order.orderItem.OrderItemRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemQueryService {
    
    private final OrderItemRepository orderItemRepository;
    
    // ========== MAIN QUERY METHODS ==========
    
    public List<OrderItemWithProductDao> findOrderItemsWithProductByOrderId(Long orderId) {
        return orderItemRepository.findOrderItemsWithProductByOrderId(orderId);
    }
    
    public List<OrderItemEntity> findByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
    
    public BigDecimal calculateOrderSubtotal(List<OrderItemEntity> orderItems) {
        return orderItems.stream()
                .map(item -> {
                    int activeQuantity = item.getQuantity() - item.getCancelledQuantity();
                    return item.getPrice().multiply(BigDecimal.valueOf(activeQuantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public boolean hasActiveItems(List<OrderItemEntity> orderItems) {
        return orderItems.stream()
                .anyMatch(item -> item.getQuantity() > item.getCancelledQuantity());
    }
    
    public boolean hasCancelledItems(List<OrderItemEntity> orderItems) {
        return orderItems.stream()
                .anyMatch(item -> item.getCancelledQuantity() > 0);
    }
}