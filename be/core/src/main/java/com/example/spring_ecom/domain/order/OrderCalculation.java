package com.example.spring_ecom.domain.order;

import java.math.BigDecimal;

public record OrderCalculation(
    BigDecimal subtotal,
    BigDecimal shippingFee,
    BigDecimal discount,
    BigDecimal total
) {
}