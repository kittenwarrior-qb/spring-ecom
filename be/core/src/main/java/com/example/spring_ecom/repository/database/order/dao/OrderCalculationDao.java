package com.example.spring_ecom.repository.database.order.dao;

import java.math.BigDecimal;

public record OrderCalculationDao(
    BigDecimal subtotal,
    BigDecimal shippingFee,
    BigDecimal discount,
    BigDecimal total
) {
}