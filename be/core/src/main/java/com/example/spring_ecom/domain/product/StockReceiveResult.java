package com.example.spring_ecom.domain.product;

import java.math.BigDecimal;

public record StockReceiveResult(
    int stockBefore,
    int stockAfter,
    BigDecimal costPrice
) {}

