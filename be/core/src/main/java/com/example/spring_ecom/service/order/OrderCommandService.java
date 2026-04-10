package com.example.spring_ecom.service.order;

import com.example.spring_ecom.controller.api.order.orderItem.model.PartialCancelRequestItem;
import com.example.spring_ecom.controller.api.order.model.CreateOrderRequestMapper;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.cart.CartItem;

import com.example.spring_ecom.repository.database.order.dao.CreateOrderFromCartDao;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderCalculation;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.PaymentStatus;
import com.example.spring_ecom.domain.order.PaymentMethod;
import com.example.spring_ecom.repository.database.order.OrderEntity;
import com.example.spring_ecom.repository.database.order.OrderEntityMapper;
import com.example.spring_ecom.repository.database.order.orderItem.OrderItemEntity;
import com.example.spring_ecom.service.notification.NotificationUseCase;
import com.example.spring_ecom.service.order.orderItem.OrderItemUseCase;
import com.example.spring_ecom.repository.database.order.OrderRepository;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import com.example.spring_ecom.repository.database.product.ProductRepository;
import com.example.spring_ecom.repository.database.user.UserRepository;
import com.example.spring_ecom.service.cart.CartUseCase;
import com.example.spring_ecom.service.inventory.InventoryUseCase;
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
    private final UserRepository userRepository;
    private final OrderItemUseCase orderItemUseCase;
    private final CartUseCase cartUseCase;
    private final OrderEntityMapper mapper;
    private final CreateOrderRequestMapper requestMapper;
    private final NotificationUseCase notificationUseCase;
    private final InventoryUseCase inventoryUseCase;
    private final ProductRepository productRepository;

    // ========== MAIN COMMAND METHODS ==========
    
    public Optional<Order> create(Order order) {
        validateUser(order.userId());
        List<CartItem> cartItems = validateAndGetCartItems(order.userId());
        
        OrderEntity entity = createOrderEntity(order);
        OrderEntity saved = orderRepository.save(entity);
        
        orderItemUseCase.createOrderItems(saved, cartItems);
        cartUseCase.clearCart(order.userId());
        
        return Optional.of(mapper.toDomain(saved));
    }
    
    public Optional<Order> createFromCart(Long userId, com.example.spring_ecom.controller.api.order.model.CreateOrderRequest request) {
        CreateOrderFromCartDao domainRequest = requestMapper.toDomain(userId, request);
        return createFromCartDomain(domainRequest);
    }
    
    public Optional<Order> createFromCartDomain(CreateOrderFromCartDao request) {
        List<CartItem> cartItems = cartUseCase.getCartItems(request.userId());
        if (cartItems.isEmpty()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Cart is empty");
        }
        
        OrderCalculation calculation = calculateOrderTotals(cartItems);
        
        // Create order entity
        OrderEntity entity = createOrderEntityFromCart(request, calculation);
        OrderEntity saved = orderRepository.save(entity);
        
        orderItemUseCase.createOrderItems(saved, cartItems);
        cartUseCase.clearCart(request.userId());
        
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
        orderItemUseCase.restoreStockForOrder(id);
        orderRepository.save(entity);
    }
    
    public Optional<Order> cancelPartial(Long orderId, List<PartialCancelRequestItem> cancelItems) {
        OrderEntity entity = findOrderById(orderId);
        validatePartialCancellation(entity);
        
        List<OrderItemEntity> orderItems = orderItemUseCase.processPartialCancellation(orderId, cancelItems);
        recalculateOrderTotals(entity, orderItems);
        updateOrderStatusAfterPartialCancel(entity, orderItems);
        
        OrderEntity updated = orderRepository.save(entity);
        return Optional.of(mapper.toDomain(updated));
    }
    
    public Optional<Order> updatePaymentStatus(Long id, PaymentStatus paymentStatus) {
        OrderEntity entity = findOrderById(id);
        validatePaymentStatusTransition(entity, paymentStatus);
        
        mapper.updatePaymentStatus(entity, paymentStatus);
        handlePaymentStatusChange(entity, paymentStatus);
        
        OrderEntity updated = orderRepository.save(entity);
        return Optional.of(mapper.toDomain(updated));
    }

    // ========== HELPER METHODS ==========
    
    private OrderEntity createOrderEntity(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        entity.setOrderNumber(generateOrderNumber());
        mapper.setInitialPaymentStatus(entity, order.paymentMethod());
        return entity;
    }
    
    private void handlePaymentStatusChange(OrderEntity entity, PaymentStatus paymentStatus) {
        log.info("[ORDER] handlePaymentStatusChange: orderId={}, paymentStatus={}, currentOrderStatus={}", 
                entity.getId(), paymentStatus, entity.getStatus());
        if (paymentStatus == PaymentStatus.PAID && entity.getStatus() == OrderStatus.PENDING) {
            log.info("[ORDER] Changing order status from PENDING to CONFIRMED due to payment");
            mapper.updateOrderStatus(entity, OrderStatus.CONFIRMED);
            // Send notification for status change
            handleStatusChange(entity, OrderStatus.CONFIRMED);
        }
    }
    
    // ========== SUPPORT METHODS ==========
    
    private OrderCalculation calculateOrderTotals(List<CartItem> cartItems) {
        BigDecimal subtotal = cartItems.stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal shippingFee = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal total = subtotal.add(shippingFee).subtract(discount);
        
        return new com.example.spring_ecom.domain.order.OrderCalculation(
                subtotal,
                shippingFee,
                discount,
                total
        );
    }
    
    private OrderEntity createOrderEntityFromCart(CreateOrderFromCartDao request, OrderCalculation calculation) {
        com.example.spring_ecom.repository.database.order.dao.CreateOrderEntityDao entityDao = 
            mapper.toCreateOrderEntityDao(request, calculation);
        
        OrderEntity entity = mapper.toEntityFromCart(entityDao);
        entity.setOrderNumber(generateOrderNumber());
        mapper.setInitialPaymentStatus(entity, request.paymentMethod());
        
        return entity;
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
        
        if (entity.getStatus() == OrderStatus.SHIPPED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Cannot cancel order that is being shipped");
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
    
    private void recalculateOrderTotals(OrderEntity order, List<OrderItemEntity> allItems) {
        BigDecimal newSubtotal = orderItemUseCase.calculateOrderSubtotal(allItems);
        order.setSubtotal(newSubtotal);
        order.setTotal(newSubtotal.add(order.getShippingFee()).subtract(order.getDiscount()));
    }
    
    private void updateOrderStatusAfterPartialCancel(OrderEntity order, List<OrderItemEntity> allItems) {
        boolean hasActiveItems = orderItemUseCase.hasActiveItems(allItems);
        boolean hasCancelledItems = orderItemUseCase.hasCancelledItems(allItems);
        
        if (!hasActiveItems) {
            mapper.cancelOrder(order);
        } else if (hasCancelledItems) {
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
        sendOrderStatusNotification(entity, status);
        
        if (status == OrderStatus.CANCELLED) {
            orderItemUseCase.restoreStockForOrder(entity.getId());
            recordReturnInTransactions(entity);
            if (entity.getPaymentStatus() == PaymentStatus.PAID) {
                mapper.updatePaymentStatus(entity, PaymentStatus.REFUNDED);
            }
        } else if (status == OrderStatus.DELIVERED) {
            orderItemUseCase.updateSoldCountForOrder(entity.getId());
            recordSaleOutTransactions(entity);
            if (entity.getPaymentMethod() == PaymentMethod.COD) {
                mapper.updatePaymentStatus(entity, PaymentStatus.PAID);
            }
        }
    }
    
    /**
     * Record SALE_OUT inventory movements when order is delivered.
     * Also consumes cost batches (FIFO) and sets cost_price on each order item.
     */
    private void recordSaleOutTransactions(OrderEntity entity) {
        try {
            List<OrderItemEntity> items = orderItemUseCase.findByOrderId(entity.getId());
            List<Long> productIds = items.stream().map(OrderItemEntity::getProductId).toList();
            Map<Long, ProductEntity> productMap = productRepository.findAllById(productIds).stream()
                    .collect(Collectors.toMap(ProductEntity::getId, Function.identity()));

            for (OrderItemEntity item : items) {
                int activeQty = item.getQuantity() - item.getCancelledQuantity();
                if (activeQty <= 0) continue;

                ProductEntity product = productMap.get(item.getProductId());
                if (product == null) continue;

                // Consume cost batches FIFO and get weighted average COGS
                BigDecimal itemCostPrice = inventoryUseCase.consumeBatchesFIFO(item.getProductId(), activeQty);
                if (itemCostPrice.compareTo(BigDecimal.ZERO) == 0) {
                    // Fallback to product-level cost price if no batches available
                    itemCostPrice = product.getCostPrice();
                }

                // Store cost_price on the order item for profit calculation
                item.setCostPrice(itemCostPrice);

                int stockAfter = product.getStockQuantity();
                int stockBefore = stockAfter; // stock already deducted at order creation

                inventoryUseCase.recordSaleOut(
                        item.getProductId(), activeQty, itemCostPrice,
                        stockBefore, stockAfter,
                        entity.getId(), entity.getOrderNumber());
            }
            log.info("[INVENTORY] Recorded SALE_OUT for order: {}", entity.getOrderNumber());
        } catch (Exception e) {
            log.error("[INVENTORY] Failed to record SALE_OUT for order {}: {}", entity.getOrderNumber(), e.getMessage());
        }
    }

    /**
     * Record RETURN_IN inventory movements when order is cancelled
     */
    private void recordReturnInTransactions(OrderEntity entity) {
        try {
            List<OrderItemEntity> items = orderItemUseCase.findByOrderId(entity.getId());
            List<Long> productIds = items.stream().map(OrderItemEntity::getProductId).toList();
            Map<Long, ProductEntity> productMap = productRepository.findAllById(productIds).stream()
                    .collect(Collectors.toMap(ProductEntity::getId, Function.identity()));

            for (OrderItemEntity item : items) {
                int activeQty = item.getQuantity() - item.getCancelledQuantity();
                if (activeQty <= 0) continue;

                ProductEntity product = productMap.get(item.getProductId());
                if (product == null) continue;

                // Stock has already been restored by restoreStockForOrder above
                int stockAfter = product.getStockQuantity();
                int stockBefore = stockAfter - activeQty;

                inventoryUseCase.recordReturnIn(
                        item.getProductId(), activeQty,
                        stockBefore, stockAfter,
                        entity.getId(), entity.getOrderNumber());
            }
            log.info("[INVENTORY] Recorded RETURN_IN for cancelled order: {}", entity.getOrderNumber());
        } catch (Exception e) {
            log.error("[INVENTORY] Failed to record RETURN_IN for order {}: {}", entity.getOrderNumber(), e.getMessage());
        }
    }

    private void sendOrderStatusNotification(OrderEntity entity, OrderStatus status) {
        if (Objects.isNull(entity.getUserId())) {
            log.warn("[NOTIFICATION] No userId for orderId={}", entity.getId());
            return;
        }
        
        try {
            String title = "Cập nhật đơn hàng";
            String message = mapStatusToMessage(entity.getOrderNumber(), status);
            String type = mapStatusToType(status);
            String actionUrl = "/orders/" + entity.getId();
            
            notificationUseCase.createAndSend(
                    entity.getUserId(),
                    type,
                    title,
                    message,
                    entity.getId(),
                    "ORDER",
                    null,
                    actionUrl
            );
            
            log.info("[ORDER] Notification sent: orderId={}, status={}", entity.getId(), status);
        } catch (Exception e) {
            log.error("[ORDER] Failed to send notification: {}", e.getMessage());
            // Don't fail the order status update if notification fails
        }
    }
    
    private String mapStatusToMessage(String orderNumber, OrderStatus status) {
        String statusText = switch (status) {
            case CONFIRMED -> "đã được xác nhận";
            case PENDING -> "đang được xử lý";
            case SHIPPED -> "đã được giao cho đơn vị vận chuyển";
            case DELIVERED -> "đã được giao thành công";
            case CANCELLED -> "đã bị hủy";
            case STOCK_RESERVED -> "đã được xác nhận tồn kho";
            default -> "đã được cập nhật";
        };
        return String.format("Đơn hàng #%s %s", orderNumber, statusText);
    }
    
    private String mapStatusToType(OrderStatus status) {
        return switch (status) {
            case CONFIRMED -> "ORDER_CONFIRMED";
            case PENDING -> "ORDER_STATUS";
            case SHIPPED -> "ORDER_SHIPPED";
            case DELIVERED -> "ORDER_DELIVERED";
            case CANCELLED -> "ORDER_CANCELLED";
            case STOCK_RESERVED -> "ORDER_CONFIRMED";
            default -> "ORDER_STATUS";
        };
    }

    
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", (int) (Math.random() * 10000));
        String orderNumber = "ORD" + timestamp + random;
        
        return orderRepository.existsByOrderNumber(orderNumber) ? generateOrderNumber() : orderNumber;
    }
}
