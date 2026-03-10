package com.example.spring_ecom.controller.api.address.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Location suggestion based on IP address")
public record LocationSuggestionResponse(
        @Schema(description = "City name", example = "Ho Chi Minh City")
        String city,
        
        @Schema(description = "Region/State name", example = "Ho Chi Minh")
        String region,
        
        @Schema(description = "Country name", example = "Vietnam")
        String country,
        
        @Schema(description = "Country code (ISO 3166-1 alpha-2)", example = "VN")
        String countryCode,
        
        @Schema(description = "Timezone", example = "Asia/Ho_Chi_Minh")
        String timezone
) {
}
