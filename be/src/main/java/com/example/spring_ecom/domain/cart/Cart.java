package com.example.spring_ecom.domain.cart;

import java.time.LocalDateTime;

public record Cart(
    Long id,
    Long userId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
