package com.example.spring_ecom.controller.api.admin.order;

import com.example.spring_ecom.controller.api.admin.order.model.UpdateOrderStatusRequest;
import com.example.spring_ecom.controller.api.order.model.OrderResponse;
import com.example.spring_ecom.controller.api.order.model.OrderResponseMapper;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderStatistics;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.PaymentStatus;
import com.example.spring_ecom.service.order.OrderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Order Management", description = "Admin APIs for managing orders")
public class AdminOrderController implements AdminOrderAPI {

    private final OrderUseCase orderUseCase;
    private final OrderResponseMapper orderResponseMapper;

    @Override
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(
            Pageable pageable, String status, String paymentStatus, 
            String search, String dateFrom, String dateTo) {
        try {
            log.info("Admin getting all orders with filters: status={}, search={}", status, search);
            LocalDate from = Objects.nonNull(dateFrom) ? LocalDate.parse(dateFrom) : null;
            LocalDate to = Objects.nonNull(dateTo) ? LocalDate.parse(dateTo) : null;

            Page<Order> orders = orderUseCase.findAllWithFilters(pageable, search, status, paymentStatus, from, to);
            Page<OrderResponse> responses = orders.map(orderResponseMapper::toResponse);
            return ResponseEntity.ok(ApiResponse.Success.of(responses));
        } catch (Exception e) {
            log.error("Error getting orders: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to get orders"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(Long orderId) {
        try {
            log.info("Admin getting order by ID: {}", orderId);
            return orderUseCase.findById(orderId)
                    .map(order -> ResponseEntity.ok(ApiResponse.Success.of(orderResponseMapper.toResponse(order))))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting order: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to get order"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        try {
            log.info("Admin updating order status: orderId={}, status={}", orderId, request.status());
            OrderStatus orderStatus = OrderStatus.valueOf(request.status().toUpperCase());
            Order updated = orderUseCase.updateOrderStatus(orderId, orderStatus);
            return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK, "Order status updated successfully",
                    orderResponseMapper.toResponse(updated)));
        } catch (IllegalArgumentException e) {
            log.error("Invalid order status: {}", request.status());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.Error.of(ResponseCode.BAD_REQUEST, "Invalid status: " + request.status()));
        } catch (Exception e) {
            log.error("Error updating order status: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to update order status"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<OrderResponse>> updatePaymentStatus(Long orderId, String paymentStatus) {
        try {
            log.info("Admin updating payment status: orderId={}, paymentStatus={}", orderId, paymentStatus);
            PaymentStatus ps = PaymentStatus.valueOf(paymentStatus.toUpperCase());
            Order updated = orderUseCase.updatePaymentStatus(orderId, ps);
            return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK, "Payment status updated successfully",
                    orderResponseMapper.toResponse(updated)));
        } catch (IllegalArgumentException e) {
            log.error("Invalid payment status: {}", paymentStatus);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.Error.of(ResponseCode.BAD_REQUEST, "Invalid payment status: " + paymentStatus));
        } catch (Exception e) {
            log.error("Error updating payment status: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to update payment status"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> cancelOrder(Long orderId, String reason) {
        try {
            log.info("Admin cancelling order: orderId={}, reason={}", orderId, reason);
            orderUseCase.cancelOrder(orderId, null, true); // isAdmin = true
            return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK, "Order cancelled successfully", null));
        } catch (Exception e) {
            log.error("Error cancelling order: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to cancel order"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<OrderStatistics>> getOrderStatistics(String period, String startDate, String endDate) {
        try {
            log.info("Admin getting order statistics: period={}", period);
            LocalDate from = Objects.nonNull(startDate) ? LocalDate.parse(startDate) : null;
            LocalDate to = Objects.nonNull(endDate) ? LocalDate.parse(endDate) : null;
            OrderStatistics statistics = orderUseCase.getOrderStatistics(period, from, to);
            return ResponseEntity.ok(ApiResponse.Success.of(statistics));
        } catch (Exception e) {
            log.error("Error getting order statistics: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to get statistics"));
        }
    }
}