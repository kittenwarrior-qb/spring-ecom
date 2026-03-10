package com.example.spring_ecom.controller.api.address.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Address response data")
public record AddressResponse(
        @Schema(description = "Address ID", example = "1")
        Long id,
        
        @Schema(description = "Full name of the recipient", example = "Nguyễn Văn A")
        String fullName,
        
        @Schema(description = "Phone number", example = "0901234567")
        String phoneNumber,
        
        @Schema(description = "Street address", example = "123 Đường Lê Lợi")
        String addressLine,
        
        @Schema(description = "Ward/Commune name", example = "Phường Bến Nghé")
        String ward,
        
        @Schema(description = "District name", example = "Quận 1")
        String district,
        
        @Schema(description = "City/Province name", example = "Hồ Chí Minh")
        String city,
        
        @Schema(description = "Postal code", example = "700000")
        String postalCode,
        
        @Schema(description = "Is this the default address", example = "true")
        Boolean isDefault,
        
        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt,
        
        @Schema(description = "Last update timestamp")
        LocalDateTime updatedAt
) {
}
