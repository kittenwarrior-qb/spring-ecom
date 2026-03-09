package com.example.spring_ecom.domain.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Product(
    Long id,
    String title,
    String slug,
    String author,
    String publisher,
    Integer publicationYear,
    String language,
    Integer pages,
    String format,
    String description,
    BigDecimal price,
    BigDecimal discountPrice,
    Integer stockQuantity,
    String coverImageUrl,
    Boolean isBestseller,
    Boolean isActive,
    Integer viewCount,
    Integer soldCount,
    BigDecimal ratingAverage,
    Integer ratingCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt
) {
}
