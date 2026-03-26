package com.example.spring_ecom.controller.api.admin.order.model;

import jakarta.validation.constraints.NotBlank;

public record UpdateOrderStatusRequest(
    @NotBlank(message = "Status is required")
    String status,
    
    String note
) {}
