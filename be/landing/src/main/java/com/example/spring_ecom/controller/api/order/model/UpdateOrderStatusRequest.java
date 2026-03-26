package com.example.spring_ecom.controller.api.order.model;

import com.example.spring_ecom.domain.order.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
    @NotNull(message = "Status is required")
    OrderStatus status
) {
}
