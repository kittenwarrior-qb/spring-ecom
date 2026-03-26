package com.example.spring_ecom.controller.api.admin;

import com.example.spring_ecom.controller.api.order.model.OrderResponse;
import com.example.spring_ecom.controller.api.order.model.OrderResponseMapper;
import com.example.spring_ecom.controller.api.order.model.UpdateOrderStatusRequest;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.service.order.OrderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminOrderController implements AdminOrderAPI {

    private final OrderUseCase orderUseCase;
    private final OrderResponseMapper responseMapper;

    @Override
    public ApiResponse<Page<OrderResponse>> getAllOrders(Pageable pageable) {
        log.info("Admin getting all orders: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Order> orders = orderUseCase.findAll(pageable);
        Page<OrderResponse> responses = orders.map(responseMapper::toResponse);
        return ApiResponse.Success.of(responses);
    }

    @Override
    public ApiResponse<OrderResponse> getOrderById(Long id) {
        log.info("Admin getting order by ID: {}", id);
        return orderUseCase.findById(id)
                .map(order -> ApiResponse.Success.of(responseMapper.toResponse(order)))
                .orElse(ApiResponse.Error.of(null, "Order not found"));
    }

    @Override
    public ApiResponse<OrderResponse> updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
        log.info("Admin updating order status: orderId={}, status={}", id, request.status());
        try {
            Order updated = orderUseCase.updateOrderStatus(id, request.status());
            return ApiResponse.Success.of(responseMapper.toResponse(updated));
        } catch (IllegalArgumentException e) {
            log.error("Invalid order status: {}", request.status());
            return ApiResponse.Error.of(null, "Invalid status: " + request.status());
        } catch (Exception e) {
            log.error("Error updating order status: {}", e.getMessage(), e);
            return ApiResponse.Error.of(null, "Failed to update order status");
        }
    }

    @Override
    public ApiResponse<Void> cancelOrder(Long id) {
        log.info("Admin cancelling order: {}", id);
        try {
            orderUseCase.cancelOrder(id, null, true);
            return ApiResponse.Success.of(null);
        } catch (Exception e) {
            log.error("Error cancelling order: {}", e.getMessage(), e);
            return ApiResponse.Error.of(null, "Failed to cancel order");
        }
    }
}
