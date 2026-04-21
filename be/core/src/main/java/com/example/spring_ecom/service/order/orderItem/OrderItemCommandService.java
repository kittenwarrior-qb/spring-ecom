package com.example.spring_ecom.service.order.orderItem;

import com.example.spring_ecom.controller.api.order.orderItem.model.PartialCancelRequestItem;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.repository.database.order.OrderEntity;
import com.example.spring_ecom.repository.database.order.orderItem.OrderItemEntity;
import com.example.spring_ecom.repository.database.order.orderItem.OrderItemEntityMapper;
import com.example.spring_ecom.repository.database.order.orderItem.OrderItemRepository;
import com.example.spring_ecom.service.product.ProductUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderItemCommandService {
    
    private final OrderItemRepository orderItemRepository;
    private final ProductUseCase productUseCase;
    private final OrderItemEntityMapper orderItemMapper;
    
    // ========== MAIN COMMAND METHODS ==========
    
    public void createOrderItems(OrderEntity orderEntity, List<CartItem> cartItems) {
        List<Long> productIds = cartItems.stream().map(CartItem::productId).toList();
        List<Product> products = validateAndGetProducts(productIds);

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::id, Function.identity()));

        List<OrderItemEntity> orderItems = cartItems.stream()
                .map(cartItem -> createOrderItem(orderEntity, cartItem, productMap))
                .toList();
        
        orderItemRepository.saveAll(orderItems);
    }
    
    public List<OrderItemEntity> processPartialCancellation(Long orderId, List<PartialCancelRequestItem> cancelItems) {
        List<OrderItemEntity> orderItems = orderItemRepository.findByOrderId(orderId);
        Map<Long, OrderItemEntity> itemMap = orderItems.stream()
                .collect(Collectors.toMap(OrderItemEntity::getId, Function.identity()));
        
        cancelItems.stream()
                .forEach(cancelItem -> {
                    OrderItemEntity orderItem = validateAndGetOrderItem(itemMap, cancelItem.orderItemId());
                    processCancelItem(orderItem, cancelItem.quantityToCancel());
                });
        
        orderItemRepository.saveAll(orderItems);
        return orderItems;
    }
    
    public void restoreStockForOrder(Long orderId) {
        List<OrderItemEntity> orderItems = orderItemRepository.findByOrderId(orderId);
        orderItems.forEach(item -> {
            int activeQuantity = item.getQuantity() - item.getCancelledQuantity();
            if (activeQuantity > 0) {
                productUseCase.updateProductStock(item.getProductId(), activeQuantity);
            }
        });
    }
    
    public void updateSoldCountForOrder(Long orderId) {
        List<OrderItemEntity> orderItems = orderItemRepository.findByOrderId(orderId);
        Map<Long, Integer> soldCountMap = new HashMap<>();
        orderItems.forEach(item -> {
            int activeQuantity = item.getQuantity() - item.getCancelledQuantity();
            if (activeQuantity > 0) {
                soldCountMap.merge(item.getProductId(), activeQuantity, Integer::sum);
            }
        });
        if (!soldCountMap.isEmpty()) {
            productUseCase.updateProductsSoldCount(soldCountMap);
        }
    }
    
    // ========== HELPER METHODS ==========
    
    private List<Product> validateAndGetProducts(List<Long> productIds) {
        List<Product> products = productUseCase.findAllByIds(productIds);
        if (products.size() != productIds.size()) {
            throw new BaseException(ResponseCode.NOT_FOUND, "Some products not found");
        }
        return products;
    }
    
    private OrderItemEntity validateAndGetOrderItem(Map<Long, OrderItemEntity> itemMap, Long orderItemId) {
        OrderItemEntity orderItem = itemMap.get(orderItemId);
        if (Objects.isNull(orderItem)) {
            throw new BaseException(ResponseCode.BAD_REQUEST, 
                    "Order item not found: " + orderItemId);
        }
        return orderItem;
    }
    
    private OrderItemEntity createOrderItem(OrderEntity order, CartItem cartItem, Map<Long, Product> productMap) {
        Product product = productMap.get(cartItem.productId());

        if (product.stockQuantity() < cartItem.quantity()) {
            throw new BaseException(ResponseCode.BAD_REQUEST,
                    "Insufficient stock for product: " + product.title());
        }
        
        // Deduct stock through UseCase
        productUseCase.updateProductStock(product.id(), -cartItem.quantity());

        return orderItemMapper.createFromCartItemDomain(order, cartItem, product);
    }
    
    private void processCancelItem(OrderItemEntity orderItem, Integer quantityToCancel) {
        int availableQuantity = orderItem.getQuantity() - orderItem.getCancelledQuantity();
        
        validateCancelQuantity(quantityToCancel, availableQuantity);
        
        orderItem.setCancelledQuantity(orderItem.getCancelledQuantity() + quantityToCancel);
        
        if (orderItem.getCancelledQuantity().equals(orderItem.getQuantity())) {
            orderItem.setStatus(com.example.spring_ecom.domain.order.OrderItem.OrderItemStatus.CANCELLED);
            orderItem.setCancelledAt(LocalDateTime.now());
        }
        
        restoreStockForQuantity(orderItem.getProductId(), quantityToCancel);
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
    
    private void restoreStockForQuantity(Long productId, Integer quantity) {
        productUseCase.updateProductStock(productId, quantity);
    }
}