package com.example.spring_ecom.domain.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank(message = "Full name is required")
        @Size(max = 100, message = "Full name must not exceed 100 characters")
        String fullName,
        
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone number must be 10-11 digits")
        String phoneNumber,
        
        @NotBlank(message = "Address line is required")
        @Size(max = 255, message = "Address line must not exceed 255 characters")
        String addressLine,
        
        @Size(max = 100, message = "Ward must not exceed 100 characters")
        String ward,
        
        @NotBlank(message = "District is required")
        @Size(max = 100, message = "District must not exceed 100 characters")
        String district,
        
        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City must not exceed 100 characters")
        String city,
        
        @Size(max = 20, message = "Postal code must not exceed 20 characters")
        String postalCode,
        
        Boolean isDefault
) {
}
