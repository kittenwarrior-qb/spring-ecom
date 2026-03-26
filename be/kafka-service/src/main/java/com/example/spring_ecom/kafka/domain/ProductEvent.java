package com.example.spring_ecom.kafka.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEvent {

    private String eventId;
    private String eventType;
    private Instant timestamp;
    private String source;

    // Product payload
    private Long productId;
    private String productTitle;
    private String productSlug;
    private Double price;
    private Double discountPrice;
    private Integer stockQuantity;
    private Integer quantityDelta;
    private Boolean isActive;
    private Long categoryId;

    // Event type constants
    public static final String CREATED       = "PRODUCT_CREATED";
    public static final String UPDATED       = "PRODUCT_UPDATED";
    public static final String DELETED       = "PRODUCT_DELETED";
    public static final String STOCK_UPDATED = "PRODUCT_STOCK_UPDATED";
}
