package com.example.spring_ecom.controller.api.order;

import com.example.spring_ecom.controller.api.order.model.CreateOrderRequest;
import com.example.spring_ecom.controller.api.order.model.OrderDetailResponse;
import com.example.spring_ecom.controller.api.order.model.OrderResponse;
import com.example.spring_ecom.controller.api.order.model.UpdateOrderStatusRequest;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.domain.order.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Order", description = "Order management APIs")
@RequestMapping("/v1/api/orders")
public interface OrderAPI {
    
    @Operation(summary = "Create new order from cart")
    @PostMapping
    ApiResponse<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request);
    
    @Operation(summary = "Get order by ID")
    @GetMapping("/{id}")
    ApiResponse<OrderResponse> getOrderById(@PathVariable Long id);
    
    @Operation(summary = "Get order detail with items")
    @GetMapping("/{id}/detail")
    ApiResponse<OrderDetailResponse> getOrderDetail(@PathVariable Long id);
    
    @Operation(summary = "Get order by order number")
    @GetMapping("/number/{orderNumber}")
    ApiResponse<OrderResponse> getOrderByNumber(@PathVariable String orderNumber);
    
    @Operation(summary = "Get current user orders")
    @GetMapping("/my-orders")
    ApiResponse<Page<OrderResponse>> getMyOrders(@Parameter(hidden = true) Pageable pageable);
    
    @Operation(summary = "Get current user orders by status")
    @GetMapping("/my-orders/status/{status}")
    ApiResponse<Page<OrderResponse>> getMyOrdersByStatus(
            @PathVariable OrderStatus status,
            @Parameter(hidden = true) Pageable pageable);
    
    @Operation(summary = "Get all orders (Admin)")
    @GetMapping
    ApiResponse<Page<OrderResponse>> getAllOrders(@Parameter(hidden = true) Pageable pageable);
    
    @Operation(summary = "Update order status (Admin)")
    @PutMapping("/{id}/status")
    ApiResponse<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request);
    
    @Operation(summary = "Cancel order")
    @PostMapping("/{id}/cancel")
    ApiResponse<Void> cancelOrder(@PathVariable Long id);
}
