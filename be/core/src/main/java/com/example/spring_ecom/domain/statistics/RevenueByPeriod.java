package com.example.spring_ecom.domain.statistics;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RevenueByPeriod(
    LocalDate date,
    Long orders,
    BigDecimal revenue,
    BigDecimal cost,
    BigDecimal profit
) {
}

