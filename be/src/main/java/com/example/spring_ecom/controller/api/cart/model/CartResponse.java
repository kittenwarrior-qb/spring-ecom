package com.example.spring_ecom.controller.api.cart.model;

import java.time.LocalDateTime;

public record CartResponse(
    Long id,
    Long userId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
