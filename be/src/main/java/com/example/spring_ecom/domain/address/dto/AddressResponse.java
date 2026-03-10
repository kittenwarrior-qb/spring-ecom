package com.example.spring_ecom.domain.address.dto;

import java.time.LocalDateTime;

public record AddressResponse(
        Long id,
        String fullName,
        String phoneNumber,
        String addressLine,
        String ward,
        String district,
        String city,
        String postalCode,
        Boolean isDefault,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
