package com.example.spring_ecom.service.order;

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
import com.example.spring_ecom.repository.database.order.OrderRepository;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import com.example.spring_ecom.repository.database.product.ProductRepository;
import com.example.spring_ecom.repository.database.user.UserEntity;
import com.example.spring_ecom.repository.database.user.UserRepository;
import com.example.spring_ecom.service.cart.CartUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCommandService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartUseCase cartUseCase;
    private final OrderEntityMapper mapper;
    
    public Order createOrder(Order order) {
        UserEntity user = userRepository.findById(order.userId())
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "User not found"));

        List<CartItem> cartItems = cartUseCase.getCartItems(order.userId());
        if (cartItems.isEmpty()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Cart is empty");
        }
        
        OrderEntity entity = mapper.toEntity(order);
        entity.setUser(user);
        entity.setOrderNumber(generateOrderNumber());
        entity.setStatus(OrderStatus.PENDING);
        
        // Set payment status based on payment method
        if (order.paymentMethod() == PaymentMethod.COD) {
            entity.setPaymentStatus(PaymentStatus.UNPAID); // COD will be paid on delivery
        } else if (order.paymentMethod() == PaymentMethod.BANK_TRANSFER) {
            entity.setPaymentStatus(PaymentStatus.PENDING); // Bank transfer needs confirmation
        }
        
        // Create order items from cart
        for (CartItem cartItem : cartItems) {
            ProductEntity product = productRepository.findById(cartItem.productId())
                    .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Product not found"));
            
            // Check stock
            if (product.getStockQuantity() < cartItem.quantity()) {
                throw new BaseException(ResponseCode.BAD_REQUEST, 
                        "Insufficient stock for product: " + product.getTitle());
            }
            
            // Reduce stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.quantity());
            productRepository.save(product);
            
            // Create order item
            OrderItemEntity orderItem = OrderItemEntity.builder()
                    .order(entity)
                    .product(product)
                    .productTitle(product.getTitle())
                    .quantity(cartItem.quantity())
                    .price(cartItem.price())
                    .subtotal(cartItem.price().multiply(BigDecimal.valueOf(cartItem.quantity())))
                    .build();
            
            entity.getItems().add(orderItem);
        }
        OrderEntity saved = orderRepository.save(entity);
        cartUseCase.clearCart(order.userId());
        
        return mapper.toDomain(saved);
    }
    
    public Order updateOrderStatus(Long id, OrderStatus status) {
        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Order not found"));
        
        if (entity.getStatus() == OrderStatus.CANCELLED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Cannot update cancelled order");
        }
        
        if (entity.getStatus() == OrderStatus.DELIVERED && status != OrderStatus.REFUNDED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Delivered order can only be refunded");
        }
        
        entity.setStatus(status);
        
        if (status == OrderStatus.CANCELLED) {
            entity.setCancelledAt(LocalDateTime.now());
            restoreStock(entity);
            // Update payment status if order is cancelled
            if (entity.getPaymentStatus() == PaymentStatus.PAID) {
                entity.setPaymentStatus(PaymentStatus.REFUNDED);
            }
        } else if (status == OrderStatus.DELIVERED) {
            updateSoldCount(entity);
            // For COD, mark as paid when delivered
            if (entity.getPaymentMethod() == PaymentMethod.COD) {
                entity.setPaymentStatus(PaymentStatus.PAID);
            }
        }
        
        OrderEntity updated = orderRepository.save(entity);
        return mapper.toDomain(updated);
    }
    
    public void cancelOrder(Long id) {
        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Order not found"));
        
        if (entity.getStatus() == OrderStatus.CANCELLED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Order already cancelled");
        }
        
        if (entity.getStatus() == OrderStatus.DELIVERED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Cannot cancel delivered order");
        }
        
        entity.setStatus(OrderStatus.CANCELLED);
        entity.setCancelledAt(LocalDateTime.now());
        
        // Update payment status if order is cancelled
        if (entity.getPaymentStatus() == PaymentStatus.PAID) {
            entity.setPaymentStatus(PaymentStatus.REFUNDED);
        }
        
        restoreStock(entity);
        orderRepository.save(entity);
    }
    
    public Order updatePaymentStatus(Long id, PaymentStatus paymentStatus) {
        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Order not found"));
        
        if (entity.getStatus() == OrderStatus.CANCELLED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Cannot update payment status for cancelled order");
        }
        
        // Validate payment status transitions
        if (entity.getPaymentStatus() == PaymentStatus.PAID && paymentStatus != PaymentStatus.REFUNDED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Paid order can only be refunded");
        }
        
        if (entity.getPaymentStatus() == PaymentStatus.REFUNDED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Cannot update refunded payment");
        }
        
        entity.setPaymentStatus(paymentStatus);
        
        // If payment is confirmed for bank transfer, update order status
        if (paymentStatus == PaymentStatus.PAID && entity.getStatus() == OrderStatus.PENDING) {
            entity.setStatus(OrderStatus.CONFIRMED);
        }
        
        OrderEntity updated = orderRepository.save(entity);
        return mapper.toDomain(updated);
    }
    
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", (int) (Math.random() * 10000));
        String orderNumber = "ORD" + timestamp + random;
        
        if (orderRepository.existsByOrderNumber(orderNumber)) {
            return generateOrderNumber();
        }
        
        return orderNumber;
    }
    
    private void restoreStock(OrderEntity order) {
        for (OrderItemEntity item : order.getItems()) {
            ProductEntity product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }
    }
    
    private void updateSoldCount(OrderEntity order) {
        for (OrderItemEntity item : order.getItems()) {
            ProductEntity product = item.getProduct();
            product.setSoldCount(product.getSoldCount() + item.getQuantity());
            productRepository.save(product);
        }
    }
}
