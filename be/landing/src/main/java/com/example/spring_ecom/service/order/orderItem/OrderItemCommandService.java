package com.example.spring_ecom.service.order.orderItem;

import com.example.spring_ecom.controller.api.order.orderItem.model.PartialCancelRequestItem;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.grpc.domain.ProductProto;
import com.example.spring_ecom.repository.database.order.OrderEntity;
import com.example.spring_ecom.repository.database.order.orderItem.OrderItemEntity;
import com.example.spring_ecom.repository.database.order.orderItem.OrderItemEntityMapper;
import com.example.spring_ecom.repository.database.order.orderItem.OrderItemRepository;
import com.example.spring_ecom.repository.grpc.ProductGrpcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderItemCommandService {
    
    private final OrderItemRepository orderItemRepository;
    private final ProductGrpcRepository productGrpcRepository;
    private final OrderItemEntityMapper orderItemMapper;
    
    // ========== MAIN COMMAND METHODS ==========
    
    /**
     * Create order items - KHÔNG dùng gRPC để update stock nữa
     * Stock sẽ được update bởi Server consumer khi nhận Kafka event
     */
    public List<OrderItemEntity> createOrderItems(OrderEntity orderEntity, List<CartItem> cartItems) {
        // Build orderItems từ proto product data
        List<OrderItemEntity> orderItems = cartItems.stream()
                .map(cartItem -> {
                    ProductProto.Product product = productGrpcRepository.getProductById(cartItem.productId())
                            .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND,
                                    "Product not found: " + cartItem.productId()));
                    return createOrderItemFromProto(orderEntity, cartItem, product);
                })
                .toList();
        
        // KHÔNG còn gọi gRPC updateProductStock ở đây nữa
        // Stock sẽ được trừ bởi Server consumer khi nhận ORDER_CREATED event
        
        orderItemRepository.saveAll(orderItems);
        return orderItems;
    }
    
    public List<OrderItemEntity> processPartialCancellation(Long orderId, List<PartialCancelRequestItem> cancelItems) {
        List<OrderItemEntity> orderItems = orderItemRepository.findByOrderId(orderId);
        Map<Long, OrderItemEntity> itemMap = orderItems.stream()
                .collect(Collectors.toMap(OrderItemEntity::getId, item -> item));
        
        cancelItems.forEach(cancelItem -> {
            OrderItemEntity orderItem = validateAndGetOrderItem(itemMap, cancelItem.orderItemId());
            processCancelItem(orderItem, cancelItem.quantityToCancel());
        });
        
        orderItemRepository.saveAll(orderItems);
        return orderItems;
    }
    
    /**
     * Restore stock - KHÔNG dùng gRPC nữa, sẽ gửi Kafka event
     * Server consumer sẽ xử lý restore stock
     */
    public List<OrderItemEntity> getOrderItemsForRestore(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
    
    public void updateSoldCountForOrder(Long orderId) {
        List<OrderItemEntity> orderItems = orderItemRepository.findByOrderId(orderId);
        Map<Long, Integer> soldMap = new HashMap<>();
        orderItems.forEach(item -> {
            int activeQty = item.getQuantity() - item.getCancelledQuantity();
            if (activeQty > 0) {
                soldMap.merge(item.getProductId(), activeQty, Integer::sum);
            }
        });
        if (!soldMap.isEmpty()) {
            productGrpcRepository.updateProductsSoldCount(soldMap);
        }
    }
    
    // ========== HELPER METHODS ==========

    private OrderItemEntity createOrderItemFromProto(OrderEntity order, CartItem cartItem, ProductProto.Product product) {
        // Dùng discountPrice nếu có, không thì dùng price
        BigDecimal price = product.getDiscountPrice() > 0
                ? BigDecimal.valueOf(product.getDiscountPrice())
                : BigDecimal.valueOf(product.getPrice());
        
        CartItem enriched = new CartItem(
                cartItem.id(),
                cartItem.cartId(),
                cartItem.productId(),
                cartItem.quantity(),
                price,
                cartItem.createdAt(),
                cartItem.updatedAt()
        );
        return orderItemMapper.createFromCartItem(order, enriched, product.getTitle(), price);
    }
    
    private OrderItemEntity validateAndGetOrderItem(Map<Long, OrderItemEntity> itemMap, Long orderItemId) {
        OrderItemEntity orderItem = itemMap.get(orderItemId);
        if (orderItem == null) {
            throw new BaseException(ResponseCode.BAD_REQUEST,
                    "Order item not found: " + orderItemId);
        }
        return orderItem;
    }
    
    private void processCancelItem(OrderItemEntity orderItem, Integer quantityToCancel) {
        int availableQuantity = orderItem.getQuantity() - orderItem.getCancelledQuantity();
        
        validateCancelQuantity(quantityToCancel, availableQuantity);
        
        orderItem.setCancelledQuantity(orderItem.getCancelledQuantity() + quantityToCancel);
        
        if (orderItem.getCancelledQuantity().equals(orderItem.getQuantity())) {
            orderItem.setStatus(com.example.spring_ecom.domain.order.OrderItem.OrderItemStatus.CANCELLED);
            orderItem.setCancelledAt(LocalDateTime.now());
        }
        
        // KHÔNG dùng gRPC updateProductStock ở đây nữa
        // Stock sẽ được restore bởi Server consumer khi nhận ORDER_CANCELLED event
    }
    
    private void validateCancelQuantity(Integer quantityToCancel, int availableQuantity) {
        if (quantityToCancel <= 0) {
            throw new BaseException(ResponseCode.BAD_REQUEST,
                    "Quantity to cancel must be greater than 0");
        }
        
        if (quantityToCancel > availableQuantity) {
            throw new BaseException(ResponseCode.BAD_REQUEST,
                    "Cannot cancel more than available quantity. Available: " + availableQuantity);
        }
    }
    
    // ========== SUPPORT FOR OrderItemUseCase ==========
    
    public BigDecimal calculateOrderSubtotal(List<OrderItemEntity> allItems) {
        return allItems.stream()
                .filter(item -> item.getStatus() != com.example.spring_ecom.domain.order.OrderItem.OrderItemStatus.CANCELLED)
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity() - item.getCancelledQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public boolean hasActiveItems(List<OrderItemEntity> allItems) {
        return allItems.stream()
                .anyMatch(item -> item.getStatus() != com.example.spring_ecom.domain.order.OrderItem.OrderItemStatus.CANCELLED);
    }
    
    public boolean hasCancelledItems(List<OrderItemEntity> allItems) {
        return allItems.stream()
                .anyMatch(item -> item.getCancelledQuantity() != null && item.getCancelledQuantity() > 0);
    }
}