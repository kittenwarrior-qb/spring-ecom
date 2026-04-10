package com.example.spring_ecom.controller.api.admin.statistics.model;

import java.math.BigDecimal;

public record InventoryValuationResponse(
    BigDecimal totalValuation
) {
}

