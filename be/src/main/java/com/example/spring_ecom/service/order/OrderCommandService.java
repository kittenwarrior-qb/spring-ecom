package com.example.spring_ecom.service.order;

import com.example.spring_ecom.controller.api.order.orderItem.model.PartialCancelRequestItem;
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
import com.example.spring_ecom.service.order.orderItem.OrderItemUseCase;
import com.example.spring_ecom.repository.database.order.OrderRepository;
import com.example.spring_ecom.repository.database.user.UserRepository;
import com.example.spring_ecom.service.cart.CartUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCommandService {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemUseCase orderItemUseCase;
    private final CartUseCase cartUseCase;
    private final OrderEntityMapper mapper;
    
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
        if (paymentStatus == PaymentStatus.PAID && entity.getStatus() == OrderStatus.PENDING) {
            mapper.updateOrderStatus(entity, OrderStatus.CONFIRMED);
        }
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
        if (status == OrderStatus.CANCELLED) {
            orderItemUseCase.restoreStockForOrder(entity.getId());
            if (entity.getPaymentStatus() == PaymentStatus.PAID) {
                mapper.updatePaymentStatus(entity, PaymentStatus.REFUNDED);
            }
        } else if (status == OrderStatus.DELIVERED) {
            orderItemUseCase.updateSoldCountForOrder(entity.getId());
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
}
