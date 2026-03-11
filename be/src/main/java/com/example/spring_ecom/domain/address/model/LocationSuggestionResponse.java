package com.example.spring_ecom.domain.address.model;

public record LocationSuggestionResponse(
        String city,
        String region,
        String country,
        String countryCode,
        String timezone
) {
}