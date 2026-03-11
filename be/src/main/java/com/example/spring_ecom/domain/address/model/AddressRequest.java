package com.example.spring_ecom.domain.address.model;

public record AddressRequest(
        String fullName,
        String phoneNumber,
        String addressLine,
        String ward,
        String district,
        String city,
        String postalCode,
        Boolean isDefault
) {
}