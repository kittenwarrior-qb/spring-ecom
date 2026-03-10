package com.example.spring_ecom.domain.address;

import java.time.LocalDateTime;

public record Address(
        Long id,
        Long userId,
        String fullName,
        String phoneNumber,
        String addressLine,
        String ward,
        String district,
        String city,
        String postalCode,
        Boolean isDefault,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
}
