package com.example.spring_ecom.controller.api.admin;

import com.example.spring_ecom.controller.api.order.model.OrderResponse;
import com.example.spring_ecom.controller.api.order.model.UpdateOrderStatusRequest;
import com.example.spring_ecom.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin Order Management", description = "Admin APIs for managing orders")
@RequestMapping("/api/admin/orders")
public interface AdminOrderAPI {

    @Operation(summary = "Get all orders", description = "Get paginated list of all orders")
    @GetMapping
    @PreAuthorize("hasAuthority('ORDER_VIEW')")
    ApiResponse<Page<OrderResponse>> getAllOrders(@Parameter(hidden = true) Pageable pageable);

    @Operation(summary = "Get order by ID", description = "Get order detail by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ORDER_VIEW')")
    ApiResponse<OrderResponse> getOrderById(@PathVariable Long id);

    @Operation(summary = "Update order status", description = "Update order status")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ORDER_UPDATE')")
    ApiResponse<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request);

    @Operation(summary = "Cancel order", description = "Cancel order")
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('ORDER_UPDATE')")
    ApiResponse<Void> cancelOrder(@PathVariable Long id);
}
