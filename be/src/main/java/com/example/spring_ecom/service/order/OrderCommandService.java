package com.example.spring_ecom.service.order;

import com.example.spring_ecom.controller.api.order.model.PartialCancelRequest.PartialCancelItem;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.PaymentStatus;
import com.example.spring_ecom.domain.order.PaymentMethod;
import com.example.spring_ecom.repository.database.order.OrderEntity;
import com.example.spring_ecom.repository.database.order.OrderEntityMapper;
import com.example.spring_ecom.repository.database.order.orderItem.OrderItemEntity;
import com.example.spring_ecom.repository.database.order.orderItem.OrderItemRepository;
import com.example.spring_ecom.repository.database.order.OrderRepository;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import com.example.spring_ecom.repository.database.product.ProductRepository;
import com.example.spring_ecom.repository.database.user.UserRepository;
import com.example.spring_ecom.service.cart.CartUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCommandService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartUseCase cartUseCase;
    private final OrderEntityMapper mapper;
    
    public Optional<Order> create(Order order) {
        validateUser(order.userId());
        List<CartItem> cartItems = validateAndGetCartItems(order.userId());
        
        OrderEntity entity = mapper.toEntity(order);
        entity.setOrderNumber(generateOrderNumber());
        mapper.setInitialPaymentStatus(entity, order.paymentMethod());
        
        // Save order first to get ID
        OrderEntity saved = orderRepository.save(entity);
        
        // Then create order items with the saved order ID
        createOrderItems(saved, cartItems);
        cartUseCase.clearCart(order.userId());
        
        return Optional.of(mapper.toDomain(saved));
    }
    
    public Optional<Order> updateStatus(Long id, OrderStatus status) {
        OrderEntity entity = findOrderById(id);
        validateStatusTransition(entity, status);
        
        mapper.updateOrderStatus(entity, status);
        handleStatusChange(entity, status);
        
        OrderEntity updated = orderRepository.save(entity);
        return Optional.of(mapper.toDomain(updated));
    }
    
    public void cancel(Long id) {
        OrderEntity entity = findOrderById(id);
        validateCancellation(entity);
        
        mapper.cancelOrder(entity);
        restoreStock(entity);
        orderRepository.save(entity);
    }
    
    public Optional<Order> cancelPartial(Long orderId, List<PartialCancelItem> cancelItems) {
        OrderEntity entity = findOrderById(orderId);
        validatePartialCancellation(entity);
        
        List<OrderItemEntity> orderItems = orderItemRepository.findByOrderId(orderId);
        Map<Long, OrderItemEntity> itemMap = orderItems.stream()
                .collect(Collectors.toMap(OrderItemEntity::getId, Function.identity()));
        
        for (PartialCancelItem cancelItem : cancelItems) {
            OrderItemEntity orderItem = itemMap.get(cancelItem.orderItemId());
            if (orderItem == null) {
                throw new BaseException(ResponseCode.BAD_REQUEST, 
                        "Order item not found: " + cancelItem.orderItemId());
            }
            
            processCancelItem(orderItem, cancelItem.quantityToCancel());
        }
        orderItemRepository.saveAll(orderItems);
        recalculateOrderTotals(entity, orderItems);
        updateOrderStatusAfterPartialCancel(entity, orderItems);
        
        OrderEntity updated = orderRepository.save(entity);
        return Optional.of(mapper.toDomain(updated));
    }
    
    private void processCancelItem(OrderItemEntity orderItem, Integer quantityToCancel) {
        int availableQuantity = orderItem.getQuantity() - orderItem.getCancelledQuantity();
        
        if (quantityToCancel <= 0) {
            throw new BaseException(ResponseCode.BAD_REQUEST, 
                    "Quantity to cancel must be greater than 0");
        }
        
        if (quantityToCancel > availableQuantity) {
            throw new BaseException(ResponseCode.BAD_REQUEST, 
                    "Cannot cancel more than available quantity. Available: " + availableQuantity);
        }
        
        orderItem.setCancelledQuantity(orderItem.getCancelledQuantity() + quantityToCancel);
        
        if (orderItem.getCancelledQuantity().equals(orderItem.getQuantity())) {
            orderItem.setStatus(com.example.spring_ecom.domain.order.OrderItemStatus.CANCELLED);
            orderItem.setCancelledAt(LocalDateTime.now());
        }
        
        restoreStockForQuantity(orderItem.getProductId(), quantityToCancel);
    }
    
    private void restoreStockForQuantity(Long productId, Integer quantity) {
        ProductEntity product = productRepository.findById(productId)
                .orElse(null);
        
        if (product != null) {
            product.setStockQuantity(product.getStockQuantity() + quantity);
            productRepository.save(product);
        }
    }
    
    public Optional<Order> updatePaymentStatus(Long id, PaymentStatus paymentStatus) {
        OrderEntity entity = findOrderById(id);
        validatePaymentStatusTransition(entity, paymentStatus);
        
        mapper.updatePaymentStatus(entity, paymentStatus);
        
        if (paymentStatus == PaymentStatus.PAID && entity.getStatus() == OrderStatus.PENDING) {
            mapper.updateOrderStatus(entity, OrderStatus.CONFIRMED);
        }
        
        OrderEntity updated = orderRepository.save(entity);
        return Optional.of(mapper.toDomain(updated));
    }
    
    private OrderEntity findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Order not found"));
    }
    
    private void validateUser(Long userId) {
        if (Objects.isNull(userId)) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "User ID is required");
        }
        
        userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "User not found"));
    }
    
    private List<CartItem> validateAndGetCartItems(Long userId) {
        List<CartItem> cartItems = cartUseCase.getCartItems(userId);
        if (cartItems.isEmpty()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Cart is empty");
        }
        return cartItems;
    }
    

    private void createOrderItems(OrderEntity orderEntity, List<CartItem> cartItems) {
        List<Long> productIds = cartItems.stream().map(CartItem::productId).toList();
        List<ProductEntity> products = productRepository.findAllById(productIds);
        
        if (products.size() != productIds.size()) {
            throw new BaseException(ResponseCode.NOT_FOUND, "Some products not found");
        }
        
        Map<Long, ProductEntity> productMap = products.stream()
                .collect(Collectors.toMap(ProductEntity::getId, Function.identity()));
        
        List<OrderItemEntity> orderItems = cartItems.stream()
                .map(cartItem -> createOrderItem(orderEntity, cartItem, productMap))
                .toList();
        
        productRepository.saveAll(products);
        orderItemRepository.saveAll(orderItems);
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
    
    private void validateStatusTransition(OrderEntity entity, OrderStatus newStatus) {
        if (entity.getStatus() == OrderStatus.CANCELLED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Cannot update cancelled order");
        }
        
        if (entity.getStatus() == OrderStatus.DELIVERED && newStatus != OrderStatus.CANCELLED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Delivered order can only be cancelled for return");
        }
    }
    
    private void validateCancellation(OrderEntity entity) {
        if (entity.getStatus() == OrderStatus.CANCELLED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Order already cancelled");
        }
        
        if (entity.getStatus() == OrderStatus.DELIVERED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Cannot cancel delivered order");
        }
    }
    
    private void validatePartialCancellation(OrderEntity entity) {
        if (entity.getStatus() == OrderStatus.CANCELLED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Order already cancelled");
        }
        
        if (entity.getStatus() == OrderStatus.DELIVERED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Cannot cancel items from delivered order");
        }
        
        if (entity.getStatus() == OrderStatus.SHIPPED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Cannot cancel items from shipped order");
        }
        
        // PARTIALLY_CANCELLED is allowed - users can continue cancelling remaining items
    }
    
    private void cancelOrderItems(List<OrderItemEntity> itemsToCancel) {
        LocalDateTime now = LocalDateTime.now();
        
        for (OrderItemEntity item : itemsToCancel) {
            if (item.getStatus() == com.example.spring_ecom.domain.order.OrderItemStatus.CANCELLED) {
                continue; // Skip already cancelled items
            }
            
            item.setStatus(com.example.spring_ecom.domain.order.OrderItemStatus.CANCELLED);
            item.setCancelledAt(now);
            item.setCancelledQuantity(item.getQuantity()); // Cancel all quantity
            
            // Restore stock for cancelled item
            restoreStockForItem(item);
        }
        
        orderItemRepository.saveAll(itemsToCancel);
    }
    
    private void restoreStockForItem(OrderItemEntity item) {
        // Only restore quantity that wasn't already cancelled
        int quantityToRestore = item.getQuantity() - item.getCancelledQuantity();
        if (quantityToRestore > 0) {
            restoreStockForQuantity(item.getProductId(), quantityToRestore);
        }
    }
    
    private void recalculateOrderTotals(OrderEntity order, List<OrderItemEntity> allItems) {
        BigDecimal newSubtotal = allItems.stream()
                .map(item -> {
                    int activeQuantity = item.getQuantity() - item.getCancelledQuantity();
                    return item.getPrice().multiply(BigDecimal.valueOf(activeQuantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        order.setSubtotal(newSubtotal);
        order.setTotal(newSubtotal.add(order.getShippingFee()).subtract(order.getDiscount()));
    }
    
    private void updateOrderStatusAfterPartialCancel(OrderEntity order, List<OrderItemEntity> allItems) {
        boolean hasActiveItems = allItems.stream()
                .anyMatch(item -> item.getQuantity() > item.getCancelledQuantity());
        
        boolean hasCancelledItems = allItems.stream()
                .anyMatch(item -> item.getCancelledQuantity() > 0);
        
        if (!hasActiveItems) {
            // All items cancelled - cancel entire order
            mapper.cancelOrder(order);
        } else if (hasCancelledItems) {
            // Some items cancelled - mark as partially cancelled
            mapper.updateOrderStatus(order, OrderStatus.PARTIALLY_CANCELLED);
        }
    }
    
    private void validatePaymentStatusTransition(OrderEntity entity, PaymentStatus newPaymentStatus) {
        if (entity.getStatus() == OrderStatus.CANCELLED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Cannot update payment status for cancelled order");
        }
        
        if (entity.getPaymentStatus() == PaymentStatus.PAID && newPaymentStatus != PaymentStatus.REFUNDED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Paid order payment can only be refunded");
        }
        
        if (entity.getPaymentStatus() == PaymentStatus.REFUNDED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Cannot update refunded payment");
        }
    }
    
    private void handleStatusChange(OrderEntity entity, OrderStatus status) {
        if (status == OrderStatus.CANCELLED) {
            restoreStock(entity);
            if (entity.getPaymentStatus() == PaymentStatus.PAID) {
                mapper.updatePaymentStatus(entity, PaymentStatus.REFUNDED);
            }
        } else if (status == OrderStatus.DELIVERED) {
            updateSoldCount(entity);
            if (entity.getPaymentMethod() == PaymentMethod.COD) {
                mapper.updatePaymentStatus(entity, PaymentStatus.PAID);
            }
        }
    }
    
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", (int) (Math.random() * 10000));
        String orderNumber = "ORD" + timestamp + random;
        
        return orderRepository.existsByOrderNumber(orderNumber) ? generateOrderNumber() : orderNumber;
    }
    
    private void restoreStock(OrderEntity order) {
        updateProductQuantities(order, (product, quantity) -> {
            product.setStockQuantity(product.getStockQuantity() + quantity);
            return product;
        });
    }
    
    private void updateSoldCount(OrderEntity order) {
        updateProductQuantities(order, (product, quantity) -> {
            product.setSoldCount(product.getSoldCount() + quantity);
            return product;
        });
    }
    
    private void updateProductQuantities(OrderEntity order, 
                                       java.util.function.BiFunction<ProductEntity, Integer, ProductEntity> productUpdater) {
        List<OrderItemEntity> orderItems = orderItemRepository.findByOrderId(order.getId());
        List<Long> productIds = orderItems.stream().map(OrderItemEntity::getProductId).toList();
        
        List<ProductEntity> products = productRepository.findAllById(productIds);
        Map<Long, ProductEntity> productMap = products.stream()
                .collect(Collectors.toMap(ProductEntity::getId, Function.identity()));
        
        List<ProductEntity> productsToUpdate = orderItems.stream()
                .map(item -> {
                    ProductEntity product = productMap.get(item.getProductId());
                    // Only restore/update quantity that wasn't already cancelled
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
