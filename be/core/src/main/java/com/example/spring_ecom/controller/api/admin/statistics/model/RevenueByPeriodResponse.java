package com.example.spring_ecom.controller.api.admin.statistics.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RevenueByPeriodResponse(
    LocalDate date,
    Long orders,
    BigDecimal revenue,
    BigDecimal cost,
    BigDecimal profit
) {
}

