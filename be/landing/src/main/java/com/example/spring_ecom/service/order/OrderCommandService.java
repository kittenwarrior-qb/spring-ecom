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
import com.example.spring_ecom.repository.grpc.coupon.CouponGrpcClient;
import com.example.spring_ecom.service.order.orderItem.OrderItemUseCase;
import com.example.spring_ecom.repository.database.order.OrderRepository;
import com.example.spring_ecom.repository.grpc.user.UserGrpcClient;
import com.example.spring_ecom.service.product.ProductUseCase;
import com.example.spring_ecom.service.cart.CartUseCase;
import com.example.spring_ecom.kafka.service.OrderKafkaProducer;
import com.example.spring_ecom.kafka.domain.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCommandService {
    
    private final OrderRepository orderRepository;
    private final UserGrpcClient userGrpcClient;
    private final ProductUseCase productUseCase;
    private final OrderItemUseCase orderItemUseCase;
    private final CartUseCase cartUseCase;
    private final OrderEntityMapper mapper;
    private final CreateOrderRequestMapper requestMapper;
    private final OrderKafkaProducer orderKafkaProducer;
    private final CouponGrpcClient couponGrpcClient;
    
    // ========== MAIN COMMAND METHODS ==========
    
    public Optional<Order> create(Order order) {
        validateUser(order.userId());
        List<CartItem> cartItems = validateAndGetCartItems(order.userId());
        
        OrderEntity entity = createOrderEntity(order);
        OrderEntity saved = orderRepository.save(entity);
        
        List<OrderItemEntity> orderItems = orderItemUseCase.createOrderItems(saved, cartItems);
        cartUseCase.clearCart(order.userId());
        
        Order domainOrder = mapper.toDomain(saved);
        publishOrderCreatedEvent(domainOrder, orderItems, cartItems);
        
        return Optional.of(domainOrder);
    }
    
    public Optional<Order> createFromCart(Long userId, com.example.spring_ecom.controller.api.order.model.CreateOrderRequest request) {
        CreateOrderFromCartDao domainRequest = requestMapper.toDomain(userId, request);
        return createFromCartDomain(domainRequest);
    }
    
    @Transactional
    public Optional<Order> createFromCartDomain(CreateOrderFromCartDao request) {
        List<CartItem> cartItems = cartUseCase.getCartItems(request.userId());
        if (cartItems.isEmpty()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Cart is empty");
        }
        
        validateStockAvailability(cartItems);
        
        OrderCalculation calculation = calculateOrderTotalsWithCoupon(cartItems, request.couponCode());
        OrderEntity entity = createOrderEntityFromCart(request, calculation);
        OrderEntity saved = orderRepository.save(entity);
        
        if (Objects.nonNull(calculation.couponId())) {
            couponGrpcClient.incrementUsage(calculation.couponId());
        }
        
        List<OrderItemEntity> orderItems = orderItemUseCase.createOrderItems(saved, cartItems);
        cartUseCase.clearCart(request.userId());
        
        Order domainOrder = mapper.toDomain(saved);
        publishOrderCreatedEvent(domainOrder, orderItems, cartItems);
        
        return Optional.of(domainOrder);
    }
    
    public Optional<Order> updateStatus(Long id, OrderStatus status) {
        OrderEntity entity = findOrderById(id);
        String oldStatus = Objects.nonNull(entity.getStatus()) ? entity.getStatus().name() : null;
        validateStatusTransition(entity, status);
        
        mapper.updateOrderStatus(entity, status);
        handleStatusChange(entity, status);
        
        OrderEntity updated = orderRepository.save(entity);
        Order domainOrder = mapper.toDomain(updated);
        
        if (status == OrderStatus.DELIVERED) {
            List<OrderItemEntity> orderItems = orderItemUseCase.findByOrderId(id);
            publishOrderDeliveredEvent(domainOrder, orderItems);
        } else {
            publishOrderEvent(domainOrder, OrderEvent.STATUS_CHANGED, oldStatus);
        }
        
        return Optional.of(domainOrder);
    }
    
    public void cancel(Long id) {
        OrderEntity entity = findOrderById(id);
        validateCancellation(entity);
        
        mapper.cancelOrder(entity);
        OrderEntity updated = orderRepository.save(entity);
        
        Order domainOrder = mapper.toDomain(updated);
        List<OrderItemEntity> orderItems = orderItemUseCase.getOrderItemsForRestore(id);
        publishOrderCancelledEvent(domainOrder, orderItems);
    }
    
    public Optional<Order> cancelPartial(Long orderId, List<PartialCancelRequestItem> cancelItems) {
        OrderEntity entity = findOrderById(orderId);
        validatePartialCancellation(entity);
        
        // Lấy items TRƯỚC khi process để biết quantity cần restore
        List<OrderItemEntity> beforeItems = orderItemUseCase.findByOrderId(orderId);
        
        List<OrderItemEntity> orderItems = orderItemUseCase.processPartialCancellation(orderId, cancelItems);
        recalculateOrderTotals(entity, orderItems);
        updateOrderStatusAfterPartialCancel(entity, orderItems);
        
        OrderEntity updated = orderRepository.save(entity);
        Order domainOrder = mapper.toDomain(updated);
        
        // Gửi Kafka event với items cần restore stock
        publishPartialCancelEvent(domainOrder, beforeItems, orderItems, cancelItems);
        
        return Optional.of(domainOrder);
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
        if (paymentStatus == PaymentStatus.PAID && entity.getStatus() == OrderStatus.PENDING) {
            mapper.updateOrderStatus(entity, OrderStatus.CONFIRMED);
        }
    }
    
    // ========== SUPPORT METHODS ==========
    
    private OrderCalculation calculateOrderTotals(List<CartItem> cartItems) {
        BigDecimal subtotal = cartItems.stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal shippingFee = BigDecimal.ZERO;
        
        return OrderCalculation.withoutCoupon(subtotal, shippingFee);
    }
    
    private OrderCalculation calculateOrderTotalsWithCoupon(List<CartItem> cartItems, String couponCode) {
        BigDecimal subtotal = cartItems.stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal shippingFee = BigDecimal.ZERO;
        
        // If no coupon code, return without discount
        if (Objects.isNull(couponCode) || couponCode.isBlank()) {
            return OrderCalculation.withoutCoupon(subtotal, shippingFee);
        }
        
        // Validate and apply coupon
        var validationResult = couponGrpcClient.validateCoupon(couponCode, subtotal);
        if (validationResult.isEmpty()) {
            log.warn("Invalid coupon code: {}", couponCode);
            return OrderCalculation.withoutCoupon(subtotal, shippingFee);
        }
        
        var result = validationResult.get();
        Long couponId = result.couponId();
        BigDecimal discount = result.discountAmount();
        
        log.info("Applied coupon {} with discount {}", couponCode, discount);
        
        return OrderCalculation.withCoupon(subtotal, shippingFee, discount, couponId);
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
        boolean valid = userGrpcClient.validateUser(userId);
        if (!valid) {
            throw new BaseException(ResponseCode.NOT_FOUND, "User not found");
        }
    }
    
    private List<CartItem> validateAndGetCartItems(Long userId) {
        List<CartItem> cartItems = cartUseCase.getCartItems(userId);
        if (cartItems.isEmpty()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Cart is empty");
        }
        return cartItems;
    }
    
    private void validateStatusTransition(OrderEntity entity, OrderStatus newStatus) {
        OrderStatus currentStatus = entity.getStatus();
        
        if (currentStatus == OrderStatus.CANCELLED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Cannot update cancelled order");
        }
        
        if (currentStatus == OrderStatus.STOCK_FAILED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Cannot update stock failed order");
        }
        
        if (currentStatus == newStatus) {
            return;
        }
        
        Set<OrderStatus> allowedNextStatus = ALLOWED_STATUS_TRANSITIONS.get(currentStatus);
        if (Objects.isNull(allowedNextStatus) || !allowedNextStatus.contains(newStatus)) {
            throw new BaseException(ResponseCode.BAD_REQUEST, 
                String.format("Invalid status transition: %s → %s. Allowed: %s", 
                    currentStatus, newStatus, allowedNextStatus));
        }
    }
    
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_STATUS_TRANSITIONS = Map.of(
        OrderStatus.PENDING, Set.of(
            OrderStatus.CONFIRMED, 
            OrderStatus.CANCELLED
        ),
        
        OrderStatus.PENDING_STOCK, Set.of(
            OrderStatus.STOCK_RESERVED, 
            OrderStatus.STOCK_FAILED, 
            OrderStatus.CANCELLED
        ),
        
        // STOCK_RESERVED → CONFIRMED, CANCELLED
        OrderStatus.STOCK_RESERVED, Set.of(
            OrderStatus.CONFIRMED, 
            OrderStatus.CANCELLED
        ),
        
        // STOCK_FAILED → CANCELLED (auto)
        OrderStatus.STOCK_FAILED, Set.of(
            OrderStatus.CANCELLED
        ),
        
        // CONFIRMED → SHIPPED, CANCELLED, PARTIALLY_CANCELLED
        OrderStatus.CONFIRMED, Set.of(
            OrderStatus.SHIPPED, 
            OrderStatus.CANCELLED,
            OrderStatus.PARTIALLY_CANCELLED
        ),
        
        // SHIPPED → DELIVERED, PARTIALLY_CANCELLED
        OrderStatus.SHIPPED, Set.of(
            OrderStatus.DELIVERED, 
            OrderStatus.PARTIALLY_CANCELLED
        ),
        
        // DELIVERED → CANCELLED (return only)
        OrderStatus.DELIVERED, Set.of(
            OrderStatus.CANCELLED
        ),
        
        // PARTIALLY_CANCELLED → CANCELLED, SHIPPED, DELIVERED
        OrderStatus.PARTIALLY_CANCELLED, Set.of(
            OrderStatus.CANCELLED,
            OrderStatus.SHIPPED,
            OrderStatus.DELIVERED
        )
    );
    
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
    
    private void recalculateOrderTotals(OrderEntity order, List<OrderItemEntity> allItems) {
        BigDecimal newSubtotal = orderItemUseCase.calculateOrderSubtotal(allItems);
        order.setSubtotal(newSubtotal);
        
        // Calculate total and ensure it's never negative
        BigDecimal totalBeforeDiscount = newSubtotal.add(order.getShippingFee());
        BigDecimal discount = order.getDiscount();
        
        // If discount exceeds total before discount, reduce discount
        if (discount.compareTo(totalBeforeDiscount) > 0) {
            discount = totalBeforeDiscount;
            order.setDiscount(discount);
        }
        
        BigDecimal newTotal = totalBeforeDiscount.subtract(discount);
        order.setTotal(newTotal);
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
        if (status == OrderStatus.CANCELLED) {
            if (entity.getPaymentStatus() == PaymentStatus.PAID) {
                mapper.updatePaymentStatus(entity, PaymentStatus.REFUNDED);
            }
        } else if (status == OrderStatus.DELIVERED) {
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

    private void publishOrderEvent(Order order, String eventType, String previousStatus) {
        try {
            OrderEvent event = OrderEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(eventType)
                    .timestamp(Instant.now())
                    .source("client")
                    .orderId(order.id())
                    .orderNumber(order.orderNumber())
                    .userId(order.userId())
                    .status(Objects.nonNull(order.status()) ? order.status().name() : null)
                    .previousStatus(previousStatus)
                    .paymentMethod(Objects.nonNull(order.paymentMethod()) ? order.paymentMethod().name() : null)
                    .paymentStatus(Objects.nonNull(order.paymentStatus()) ? order.paymentStatus().name() : null)
                    .total(Objects.nonNull(order.total()) ? order.total().doubleValue() : 0.0)
                    .build();
            orderKafkaProducer.send(event);
        } catch (Exception e) {
            log.error("Failed to build/send order event: {}", e.getMessage());
        }
    }
    
    // ========== NEW KAFKA EVENT METHODS ==========

    private void validateStockAvailability(List<CartItem> cartItems) {
        productUseCase.validateStockForOrder(cartItems);
    }

    private void publishOrderCreatedEvent(Order order, List<OrderItemEntity> orderItems, List<CartItem> cartItems) {
        try {
            List<OrderEvent.OrderItemPayload> itemPayloads = orderItems.stream()
                    .map(item -> OrderEvent.OrderItemPayload.builder()
                            .productId(item.getProductId())
                            .productTitle(item.getProductTitle())
                            .quantity(item.getQuantity())
                            .price(item.getPrice().doubleValue())
                            .subtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue())
                            .build())
                    .collect(Collectors.toList());
            
            OrderEvent event = OrderEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(OrderEvent.CREATED)
                    .timestamp(Instant.now())
                    .source("client")
                    .orderId(order.id())
                    .orderNumber(order.orderNumber())
                    .userId(order.userId())
                    .status(Objects.nonNull(order.status()) ? order.status().name() : null)
                    .paymentMethod(Objects.nonNull(order.paymentMethod()) ? order.paymentMethod().name() : null)
                    .paymentStatus(Objects.nonNull(order.paymentStatus()) ? order.paymentStatus().name() : null)
                    .subtotal(Objects.nonNull(order.subtotal()) ? order.subtotal().doubleValue() : 0.0)
                    .shippingFee(Objects.nonNull(order.shippingFee()) ? order.shippingFee().doubleValue() : 0.0)
                    .discount(Objects.nonNull(order.discount()) ? order.discount().doubleValue() : 0.0)
                    .total(Objects.nonNull(order.total()) ? order.total().doubleValue() : 0.0)
                    .items(itemPayloads)
                    .build();
            
            log.info("[KAFKA] Sending ORDER_CREATED event - OrderId: {}, Items: {}, Total: {}",
                    order.id(), itemPayloads.size(), event.getTotal());
            orderKafkaProducer.send(event);
        } catch (Exception e) {
            log.error("Failed to send ORDER_CREATED event: {}", e.getMessage());
        }
    }

    private void publishOrderCancelledEvent(Order order, List<OrderItemEntity> orderItems) {
        try {
            List<OrderEvent.OrderItemPayload> itemPayloads = orderItems.stream()
                    .filter(item -> item.getQuantity() - item.getCancelledQuantity() > 0)
                    .map(item -> {
                        int activeQty = item.getQuantity() - item.getCancelledQuantity();
                        return OrderEvent.OrderItemPayload.builder()
                                .productId(item.getProductId())
                                .productTitle(item.getProductTitle())
                                .quantity(activeQty)
                                .price(item.getPrice().doubleValue())
                                .subtotal(item.getPrice().multiply(BigDecimal.valueOf(activeQty)).doubleValue())
                                .build();
                    })
                    .collect(Collectors.toList());
            
            OrderEvent event = OrderEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(OrderEvent.CANCELLED)
                    .timestamp(Instant.now())
                    .source("client")
                    .orderId(order.id())
                    .orderNumber(order.orderNumber())
                    .userId(order.userId())
                    .status(OrderStatus.CANCELLED.name())
                    .paymentMethod(Objects.nonNull(order.paymentMethod()) ? order.paymentMethod().name() : null)
                    .paymentStatus(Objects.nonNull(order.paymentStatus()) ? order.paymentStatus().name() : null)
                    .total(Objects.nonNull(order.total()) ? order.total().doubleValue() : 0.0)
                    .items(itemPayloads)
                    .build();
            
            log.info("📤 [KAFKA] Sending ORDER_CANCELLED event - OrderId: {}, Items to restore: {}",
                    order.id(), itemPayloads.size());
            orderKafkaProducer.send(event);
        } catch (Exception e) {
            log.error("Failed to send ORDER_CANCELLED event: {}", e.getMessage());
        }
    }
    
    /**
     * Publish PARTIAL_CANCEL event với items cần restore stock
     * Server consumer sẽ: restore stock cho các items bị cancel
     */
    private void publishPartialCancelEvent(Order order, 
                                           List<OrderItemEntity> beforeItems, 
                                           List<OrderItemEntity> afterItems,
                                           List<PartialCancelRequestItem> cancelItems) {
        try {
            // Map orderItemId -> quantityToCancel
            Map<Long, Integer> cancelMap = cancelItems.stream()
                    .collect(Collectors.toMap(PartialCancelRequestItem::orderItemId, PartialCancelRequestItem::quantityToCancel));
            
            // Build items cần restore stock
            List<OrderEvent.OrderItemPayload> itemPayloads = new ArrayList<>();
            for (OrderItemEntity item : afterItems) {
                Integer qtyToCancel = cancelMap.get(item.getId());
                if (Objects.nonNull(qtyToCancel) && qtyToCancel > 0) {
                    itemPayloads.add(OrderEvent.OrderItemPayload.builder()
                            .productId(item.getProductId())
                            .productTitle(item.getProductTitle())
                            .quantity(qtyToCancel)
                            .price(item.getPrice().doubleValue())
                            .subtotal(item.getPrice().multiply(BigDecimal.valueOf(qtyToCancel)).doubleValue())
                            .build());
                }
            }
            
            OrderEvent event = OrderEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("ORDER_PARTIAL_CANCELLED")
                    .timestamp(Instant.now())
                    .source("client")
                    .orderId(order.id())
                    .orderNumber(order.orderNumber())
                    .userId(order.userId())
                    .status(Objects.nonNull(order.status()) ? order.status().name() : null)
                    .paymentMethod(Objects.nonNull(order.paymentMethod()) ? order.paymentMethod().name() : null)
                    .paymentStatus(Objects.nonNull(order.paymentStatus()) ? order.paymentStatus().name() : null)
                    .total(Objects.nonNull(order.total()) ? order.total().doubleValue() : 0.0)
                    .items(itemPayloads)
                    .build();
            
            log.info("📤 [KAFKA] Sending ORDER_PARTIAL_CANCELLED event - OrderId: {}, Items to restore: {}",
                    order.id(), itemPayloads.size());
            orderKafkaProducer.send(event);
        } catch (Exception e) {
            log.error("Failed to send ORDER_PARTIAL_CANCELLED event: {}", e.getMessage());
        }
    }
    
    /**
     * Publish ORDER_DELIVERED event với items
     * Server consumer sẽ: update sold count cho từng sản phẩm
     */
    private void publishOrderDeliveredEvent(Order order, List<OrderItemEntity> orderItems) {
        try {
            List<OrderEvent.OrderItemPayload> itemPayloads = orderItems.stream()
                    .filter(item -> item.getStatus() != com.example.spring_ecom.domain.order.OrderItem.OrderItemStatus.CANCELLED)
                    .map(item -> {
                        int activeQty = item.getQuantity() - item.getCancelledQuantity();
                        return OrderEvent.OrderItemPayload.builder()
                                .productId(item.getProductId())
                                .productTitle(item.getProductTitle())
                                .quantity(activeQty)
                                .price(item.getPrice().doubleValue())
                                .subtotal(item.getPrice().multiply(BigDecimal.valueOf(activeQty)).doubleValue())
                                .build();
                    })
                    .collect(Collectors.toList());
            
            OrderEvent event = OrderEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(OrderEvent.DELIVERED)
                    .timestamp(Instant.now())
                    .source("client")
                    .orderId(order.id())
                    .orderNumber(order.orderNumber())
                    .userId(order.userId())
                    .status(OrderStatus.DELIVERED.name())
                    .paymentMethod(Objects.nonNull(order.paymentMethod()) ? order.paymentMethod().name() : null)
                    .paymentStatus(Objects.nonNull(order.paymentStatus()) ? order.paymentStatus().name() : null)
                    .total(Objects.nonNull(order.total()) ? order.total().doubleValue() : 0.0)
                    .items(itemPayloads)
                    .build();
            
            log.info("📤 [KAFKA] Sending ORDER_DELIVERED event - OrderId: {}, Items: {}",
                    order.id(), itemPayloads.size());
            orderKafkaProducer.send(event);
        } catch (Exception e) {
            log.error("Failed to send ORDER_DELIVERED event: {}", e.getMessage());
        }
    }
    
    /**
     * Send ORDER_PAID event to server to deduct stock (convert reserved → sold)
     */
    public void sendOrderPaidEvent(Order order) {
        try {
            List<OrderItemEntity> orderItems = orderItemUseCase.findByOrderId(order.id());
            
            List<OrderEvent.OrderItemPayload> itemPayloads = orderItems.stream()
                    .filter(item -> Objects.isNull(item.getStatus()) || 
                            item.getStatus() == com.example.spring_ecom.domain.order.OrderItem.OrderItemStatus.ACTIVE)
                    .map(item -> OrderEvent.OrderItemPayload.builder()
                            .productId(item.getProductId())
                            .productTitle(item.getProductTitle())
                            .quantity(item.getQuantity())
                            .price(item.getPrice().doubleValue())
                            .subtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue())
                            .build())
                    .collect(Collectors.toList());
            
            OrderEvent event = OrderEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("ORDER_PAID")
                    .timestamp(Instant.now())
                    .source("client")
                    .orderId(order.id())
                    .orderNumber(order.orderNumber())
                    .userId(order.userId())
                    .status(OrderStatus.CONFIRMED.name())
                    .paymentStatus(PaymentStatus.PAID.name())
                    .items(itemPayloads)
                    .build();
            
            log.info("📤 [KAFKA] Sending ORDER_PAID event for stock deduction - OrderId: {}", order.id());
            orderKafkaProducer.send(event);
        } catch (Exception e) {
            log.error("Failed to send ORDER_PAID event for order: {}", order.id(), e);
        }
    }
}
