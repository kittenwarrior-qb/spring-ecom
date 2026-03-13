package com.example.spring_ecom.service.order.orderItem;

import com.example.spring_ecom.controller.api.order.orderItem.model.PartialCancelRequestItem;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.repository.database.order.OrderEntity;
import com.example.spring_ecom.repository.database.order.orderItem.OrderItemEntity;
import com.example.spring_ecom.repository.database.order.orderItem.OrderItemRepository;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import com.example.spring_ecom.repository.database.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private final ProductRepository productRepository;
    
    // ========== MAIN COMMAND METHODS ==========
    
    public void createOrderItems(OrderEntity orderEntity, List<CartItem> cartItems) {
        List<Long> productIds = cartItems.stream().map(CartItem::productId).toList();
        List<ProductEntity> products = validateAndGetProducts(productIds);
        
        Map<Long, ProductEntity> productMap = products.stream()
                .collect(Collectors.toMap(ProductEntity::getId, Function.identity()));
        
        List<OrderItemEntity> orderItems = cartItems.stream()
                .map(cartItem -> createOrderItem(orderEntity, cartItem, productMap))
                .toList();
        
        productRepository.saveAll(products);
        orderItemRepository.saveAll(orderItems);
    }
    
    public List<OrderItemEntity> processPartialCancellation(Long orderId, List<PartialCancelRequestItem> cancelItems) {
        List<OrderItemEntity> orderItems = orderItemRepository.findByOrderId(orderId);
        Map<Long, OrderItemEntity> itemMap = orderItems.stream()
                .collect(Collectors.toMap(OrderItemEntity::getId, Function.identity()));
        
        for (PartialCancelRequestItem cancelItem : cancelItems) {
            OrderItemEntity orderItem = validateAndGetOrderItem(itemMap, cancelItem.orderItemId());
            processCancelItem(orderItem, cancelItem.quantityToCancel());
        }
        
        orderItemRepository.saveAll(orderItems);
        return orderItems;
    }
    
    public void restoreStockForOrder(Long orderId) {
        List<OrderItemEntity> orderItems = orderItemRepository.findByOrderId(orderId);
        updateProductQuantities(orderItems, (product, quantity) -> {
            product.setStockQuantity(product.getStockQuantity() + quantity);
            return product;
        });
    }
    
    public void updateSoldCountForOrder(Long orderId) {
        List<OrderItemEntity> orderItems = orderItemRepository.findByOrderId(orderId);
        updateProductQuantities(orderItems, (product, quantity) -> {
            product.setSoldCount(product.getSoldCount() + quantity);
            return product;
        });
    }
    
    // ========== HELPER METHODS ==========
    
    private List<ProductEntity> validateAndGetProducts(List<Long> productIds) {
        List<ProductEntity> products = productRepository.findAllById(productIds);
        if (products.size() != productIds.size()) {
            throw new BaseException(ResponseCode.NOT_FOUND, "Some products not found");
        }
        return products;
    }
    
    private OrderItemEntity validateAndGetOrderItem(Map<Long, OrderItemEntity> itemMap, Long orderItemId) {
        OrderItemEntity orderItem = itemMap.get(orderItemId);
        if (orderItem == null) {
            throw new BaseException(ResponseCode.BAD_REQUEST, 
                    "Order item not found: " + orderItemId);
        }
        return orderItem;
    }
    
    private OrderItemEntity createOrderItem(OrderEntity order, CartItem cartItem, Map<Long, ProductEntity> productMap) {
        ProductEntity product = productMap.get(cartItem.productId());
        
        if (product.getStockQuantity() < cartItem.quantity()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, 
                    "Insufficient stock for product: " + product.getTitle());
        }
        
        product.setStockQuantity(product.getStockQuantity() - cartItem.quantity());
        
        return OrderItemEntity.builder()
                .orderId(order.getId())
                .productId(product.getId())
                .productTitle(product.getTitle())
                .quantity(cartItem.quantity())
                .price(cartItem.price())
                .subtotal(cartItem.price().multiply(BigDecimal.valueOf(cartItem.quantity())))
                .build();
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
        ProductEntity product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            product.setStockQuantity(product.getStockQuantity() + quantity);
            productRepository.save(product);
        }
    }
    
    private void updateProductQuantities(List<OrderItemEntity> orderItems, 
                                       java.util.function.BiFunction<ProductEntity, Integer, ProductEntity> productUpdater) {
        List<Long> productIds = orderItems.stream().map(OrderItemEntity::getProductId).toList();
        List<ProductEntity> products = productRepository.findAllById(productIds);
        Map<Long, ProductEntity> productMap = products.stream()
                .collect(Collectors.toMap(ProductEntity::getId, Function.identity()));
        
        List<ProductEntity> productsToUpdate = orderItems.stream()
                .map(item -> {
                    ProductEntity product = productMap.get(item.getProductId());
                    int activeQuantity = item.getQuantity() - item.getCancelledQuantity();
                    return Objects.nonNull(product) && activeQuantity > 0 
                        ? productUpdater.apply(product, activeQuantity) 
                        : null;
                })
                .filter(Objects::nonNull)
                .toList();
        
        productRepository.saveAll(productsToUpdate);
    }
}