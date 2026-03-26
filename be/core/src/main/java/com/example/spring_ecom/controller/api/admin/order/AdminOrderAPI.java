package com.example.spring_ecom.controller.api.admin.order;

import com.example.spring_ecom.controller.api.admin.order.model.UpdateOrderStatusRequest;
import com.example.spring_ecom.controller.api.order.model.OrderResponse;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.domain.order.OrderStatistics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/api/admin/orders")
@Tag(name = "Admin Order Management", description = "Admin APIs for managing orders")
public interface AdminOrderAPI {

    @Operation(summary = "Get all orders", description = "Get paginated list of all orders with filters")
    @GetMapping
    @PreAuthorize("hasAuthority('ORDER_VIEW')")
    ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(
            Pageable pageable,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by payment status") @RequestParam(required = false) String paymentStatus,
            @Parameter(description = "Search by order number or user") @RequestParam(required = false) String search,
            @Parameter(description = "Filter from date (yyyy-MM-dd)") @RequestParam(required = false) String dateFrom,
            @Parameter(description = "Filter to date (yyyy-MM-dd)") @RequestParam(required = false) String dateTo);

    @Operation(summary = "Get order by ID", description = "Get order detail by ID")
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAuthority('ORDER_VIEW')")
    ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @Parameter(description = "Order ID") @PathVariable Long orderId);

    @Operation(summary = "Update order status", description = "Update order status")
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasAuthority('ORDER_UPDATE')")
    ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request);

    @Operation(summary = "Update payment status", description = "Update payment status")
    @PutMapping("/{orderId}/payment-status")
    @PreAuthorize("hasAuthority('ORDER_UPDATE')")
    ResponseEntity<ApiResponse<OrderResponse>> updatePaymentStatus(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            @Parameter(description = "New payment status") @RequestParam String paymentStatus);

    @Operation(summary = "Cancel order", description = "Cancel order")
    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasAuthority('ORDER_UPDATE')")
    ResponseEntity<ApiResponse<Void>> cancelOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            @Parameter(description = "Cancellation reason") @RequestParam(required = false) String reason);

    @Operation(summary = "Get order statistics", description = "Get order statistics and analytics")
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('ORDER_VIEW')")
    ResponseEntity<ApiResponse<OrderStatistics>> getOrderStatistics(
            @Parameter(description = "Period: daily, weekly, monthly, yearly") @RequestParam(defaultValue = "monthly") String period,
            @Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam(required = false) String startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam(required = false) String endDate);
}
