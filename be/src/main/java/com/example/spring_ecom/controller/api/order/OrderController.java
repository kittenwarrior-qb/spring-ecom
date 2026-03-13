package com.example.spring_ecom.controller.api.order;

import com.example.spring_ecom.controller.api.order.model.CreateOrderRequest;
import com.example.spring_ecom.controller.api.order.model.OrderResponse;
import com.example.spring_ecom.controller.api.order.model.OrderResponseMapper;
import com.example.spring_ecom.controller.api.order.model.UpdateOrderStatusRequest;
import com.example.spring_ecom.controller.api.order.orderItem.model.OrderDetailResponse;
import com.example.spring_ecom.controller.api.order.orderItem.model.PartialCancelRequest;
import com.example.spring_ecom.controller.api.payment.model.PaymentInfoResponse;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.SecurityUtil;
import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.service.cart.CartUseCase;

import com.example.spring_ecom.service.order.payment.PaymentQRService;
import com.example.spring_ecom.service.order.OrderUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController implements OrderAPI {
    
    private final OrderUseCase orderUseCase;
    private final CartUseCase cartUseCase;
    private final OrderResponseMapper responseMapper;
    private final PaymentQRService paymentQRService;
    
    @Override
    public ApiResponse<OrderResponse> createOrder(CreateOrderRequest request) {
        return createOrderInternal(request);
    }
    
    @Override
    public ApiResponse<OrderResponse> createOrderFromCart(CreateOrderRequest request) {
        return createOrderInternal(request);
    }
    
    private ApiResponse<OrderResponse> createOrderInternal(CreateOrderRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        
        List<CartItem> cartItems = cartUseCase.getCartItems(userId);
        if (cartItems.isEmpty()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Cart is empty");
        }
        
        BigDecimal subtotal = cartItems.stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal shippingFee = BigDecimal.ZERO;
        BigDecimal total = subtotal.add(shippingFee);
        
        Order order = new Order(
                null,
                null,
                userId,
                OrderStatus.PENDING,
                null, 
                subtotal,
                shippingFee,
                BigDecimal.ZERO,
                total,
                request.paymentMethod(),
                request.shippingAddress(),
                request.shippingCity(),
                request.shippingDistrict(),
                request.shippingWard(),
                request.recipientName(),
                request.recipientPhone(),
                request.note(),
                null,
                null,
                null
        );
        
        Order created = orderUseCase.createOrder(order);
        return ApiResponse.Success.of(responseMapper.toResponse(created));
    }
    
    @Override
    public ApiResponse<OrderResponse> getOrderById(Long id) {
        OrderResponse order = orderUseCase.findById(id)
                .map(responseMapper::toResponse)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Order not found"));
        return ApiResponse.Success.of(order);
    }
    
    @Override
    public ApiResponse<OrderDetailResponse> getOrderDetail(Long id) {
        OrderDetailResponse detail = orderUseCase.getOrderDetail(id);
        return ApiResponse.Success.of(detail);
    }
    
    @Override
    public ApiResponse<OrderResponse> getOrderByNumber(String orderNumber) {
        OrderResponse order = orderUseCase.findByOrderNumber(orderNumber)
                .map(responseMapper::toResponse)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Order not found"));
        return ApiResponse.Success.of(order);
    }
    
    @Override
    public ApiResponse<Page<OrderResponse>> getMyOrders(Pageable pageable) {
        Long userId = SecurityUtil.getCurrentUserId();
        Page<OrderResponse> orders = orderUseCase.findByUserId(userId, pageable)
                .map(responseMapper::toResponse);
        return ApiResponse.Success.of(orders);
    }
    
    @Override
    public ApiResponse<Page<OrderDetailResponse>> getMyOrdersWithItems(Pageable pageable) {
        Long userId = SecurityUtil.getCurrentUserId();
        Page<Order> orders = orderUseCase.findByUserId(userId, pageable);
        
        Page<OrderDetailResponse> ordersWithItems = orders.map(order -> 
            orderUseCase.getOrderDetail(order.id())
        );
        
        return ApiResponse.Success.of(ordersWithItems);
    }
    
    @Override
    public ApiResponse<Page<OrderResponse>> getMyOrdersByStatus(OrderStatus status, Pageable pageable) {
        Long userId = SecurityUtil.getCurrentUserId();
        Page<OrderResponse> orders = orderUseCase.findByUserIdAndStatus(userId, status, pageable)
                .map(responseMapper::toResponse);
        return ApiResponse.Success.of(orders);
    }
    
    @Override
    public ApiResponse<Page<OrderDetailResponse>> getMyOrdersByStatusWithItems(OrderStatus status, Pageable pageable) {
        Long userId = SecurityUtil.getCurrentUserId();
        Page<Order> orders = orderUseCase.findByUserIdAndStatus(userId, status, pageable);
        
        Page<OrderDetailResponse> ordersWithItems = orders.map(order -> 
            orderUseCase.getOrderDetail(order.id())
        );
        
        return ApiResponse.Success.of(ordersWithItems);
    }
    
    @Override
    public ApiResponse<Page<OrderResponse>> getAllOrders(Pageable pageable) {
        Page<OrderResponse> orders = orderUseCase.findAll(pageable)
                .map(responseMapper::toResponse);
        return ApiResponse.Success.of(orders);
    }
    
    @Override
    public ApiResponse<OrderResponse> updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = orderUseCase.updateOrderStatus(id, request.status());
        return ApiResponse.Success.of(responseMapper.toResponse(order));
    }
    
    @Override
    public ApiResponse<Void> cancelOrder(Long id) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        boolean isAdmin = SecurityUtil.hasRole("ADMIN");
        
        if (!isAdmin) {
            Order order = orderUseCase.findById(id)
                    .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Order not found"));
            
            if (!order.userId().equals(currentUserId)) {
                throw new BaseException(ResponseCode.FORBIDDEN, "You can only cancel your own orders");
            }
        }
        
        orderUseCase.cancelOrder(id);
        return ApiResponse.Success.of(ResponseCode.ORDER_CANCELLED);
    }
    
    @Override
    public ApiResponse<OrderResponse> partialCancelOrder(Long id, PartialCancelRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        boolean isAdmin = SecurityUtil.hasRole("ADMIN");
        
        if (!isAdmin) {
            Order order = orderUseCase.findById(id)
                    .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Order not found"));
            
            if (!order.userId().equals(currentUserId)) {
                throw new BaseException(ResponseCode.FORBIDDEN, "You can only cancel your own orders");
            }
        }
        
        Order updatedOrder = orderUseCase.cancelPartialOrder(id, request.items());
        return ApiResponse.Success.of(responseMapper.toResponse(updatedOrder));
    }
    
    // Payment related endpoints
    @Override
    public ApiResponse<PaymentInfoResponse> createPaymentInfo(String orderNumber) {
        log.info("Creating payment info for order: {}", orderNumber);
        
        PaymentInfoResponse paymentInfo = paymentQRService.createPaymentInfo(orderNumber);
        
        return ApiResponse.Success.of(paymentInfo);
    }
    
    @Override
    public ApiResponse<String> getPaymentStatus(String orderNumber) {
        log.info("Checking payment status for order: {}", orderNumber);
        
        return ApiResponse.Success.of("PENDING");
    }
}
