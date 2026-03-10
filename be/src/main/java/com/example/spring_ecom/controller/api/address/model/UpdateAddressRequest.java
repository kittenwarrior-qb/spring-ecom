package com.example.spring_ecom.controller.api.address.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for updating an existing address")
public record UpdateAddressRequest(
        @Schema(description = "Full name of the recipient", example = "Nguyễn Văn A", required = true)
        @NotBlank(message = "Full name is required")
        @Size(max = 100, message = "Full name must not exceed 100 characters")
        String fullName,
        
        @Schema(description = "Phone number (10-11 digits)", example = "0901234567", required = true)
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone number must be 10-11 digits")
        String phoneNumber,
        
        @Schema(description = "Street address with house number", example = "123 Đường Lê Lợi", required = true)
        @NotBlank(message = "Address line is required")
        @Size(max = 255, message = "Address line must not exceed 255 characters")
        String addressLine,
        
        @Schema(description = "Ward/Commune name", example = "Phường Bến Nghé")
        @Size(max = 100, message = "Ward must not exceed 100 characters")
        String ward,
        
        @Schema(description = "District name", example = "Quận 1", required = true)
        @NotBlank(message = "District is required")
        @Size(max = 100, message = "District must not exceed 100 characters")
        String district,
        
        @Schema(description = "City/Province name", example = "Hồ Chí Minh", required = true)
        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City must not exceed 100 characters")
        String city,
        
        @Schema(description = "Postal code", example = "700000")
        @Size(max = 20, message = "Postal code must not exceed 20 characters")
        String postalCode,
        
        @Schema(description = "Set this address as default", example = "true")
        Boolean isDefault
) {
}
