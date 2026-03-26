package com.example.spring_ecom.controller.api.order.model;

import com.example.spring_ecom.domain.order.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod,
    
    @NotBlank(message = "Shipping address is required")
    String shippingAddress,
    
    @NotBlank(message = "Shipping city is required")
    String shippingCity,
    
    @NotBlank(message = "Shipping district is required")
    String shippingDistrict,
    
    @NotBlank(message = "Shipping ward is required")
    String shippingWard,
    
    @NotBlank(message = "Recipient name is required")
    String recipientName,
    
    @NotBlank(message = "Recipient phone is required")
    String recipientPhone,
    
    String note,
    
    String couponCode
) {
}
