package com.example.spring_ecom.domain.userInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserInfo(
        Long id,
        Long userId,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate dateOfBirth,
        String avatarUrl,
        String address,
        String ward,
        String district,
        String city,
        String postalCode,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
}