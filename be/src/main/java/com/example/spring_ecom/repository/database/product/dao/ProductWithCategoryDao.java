package com.example.spring_ecom.repository.database.product.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductWithCategoryDao(
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
    Long categoryId,
    String categoryName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt
) {
}