package com.example.spring_ecom.controller.api.admin.inventory.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePurchaseOrderRequest {

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    private String note;

    @NotNull(message = "Items are required")
    @Valid
    private List<PurchaseOrderItemRequest> items;
}

