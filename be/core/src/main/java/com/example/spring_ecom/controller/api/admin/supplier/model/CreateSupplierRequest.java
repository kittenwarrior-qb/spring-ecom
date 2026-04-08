package com.example.spring_ecom.controller.api.admin.supplier.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSupplierRequest {
    
    @NotBlank(message = "Supplier name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;
    
    @Size(max = 255, message = "Contact name must not exceed 255 characters")
    private String contactName;
    
    @Size(max = 50, message = "Phone must not exceed 50 characters")
    private String phone;
    
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
    
    private String address;
    
    private String note;
    
    private Boolean isActive = true;
}

