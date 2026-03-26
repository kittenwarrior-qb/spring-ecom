package com.example.spring_ecom.controller.grpc.product;

import com.example.spring_ecom.grpc.domain.ProductProto;
import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.domain.category.Category;
import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Component
public class ProductGrpcMapper {
    
    public ProductProto.Product toProto(Product product) {
        ProductProto.Product.Builder builder = ProductProto.Product.newBuilder()
                .setId(product.id())
                .setTitle(product.title() != null ? product.title() : "")
                .setSlug(product.slug() != null ? product.slug() : "")
                .setAuthor(product.author() != null ? product.author() : "")
                .setLanguage(product.language() != null ? product.language() : "")
                .setFormat(product.format() != null ? product.format() : "")
                .setPrice(product.price() != null ? product.price().doubleValue() : 0)
                .setStockQuantity(product.stockQuantity() != null ? product.stockQuantity() : 0)
                .setIsBestseller(product.isBestseller() != null ? product.isBestseller() : false)
                .setIsActive(product.isActive() != null ? product.isActive() : false)
                .setViewCount(product.viewCount() != null ? product.viewCount() : 0)
                .setSoldCount(product.soldCount() != null ? product.soldCount() : 0)
                .setRatingAverage(product.ratingAverage() != null ? product.ratingAverage().doubleValue() : 0)
                .setRatingCount(product.ratingCount() != null ? product.ratingCount() : 0);
        
        if (product.publisher() != null) {
            builder.setPublisher(product.publisher());
        }
        if (product.publicationYear() != null) {
            builder.setPublicationYear(product.publicationYear());
        }
        if (product.pages() != null) {
            builder.setPages(product.pages());
        }
        if (product.description() != null) {
            builder.setDescription(product.description());
        }
        if (product.discountPrice() != null) {
            builder.setDiscountPrice(product.discountPrice().doubleValue());
        }
        if (product.coverImageUrl() != null) {
            builder.setCoverImageUrl(product.coverImageUrl());
        }
        if (product.categoryId() != null) {
            builder.setCategoryId(product.categoryId());
        }
        if (product.createdAt() != null) {
            Instant instant = product.createdAt().toInstant(java.time.ZoneOffset.UTC);
            builder.setCreatedAt(Timestamp.newBuilder()
                    .setSeconds(instant.getEpochSecond())
                    .setNanos(instant.getNano())
                    .build());
        }
        if (product.updatedAt() != null) {
            Instant instant = product.updatedAt().toInstant(java.time.ZoneOffset.UTC);
            builder.setUpdatedAt(Timestamp.newBuilder()
                    .setSeconds(instant.getEpochSecond())
                    .setNanos(instant.getNano())
                    .build());
        }
        
        return builder.build();
    }
    
    public ProductProto.Category toProto(Category category) {
        ProductProto.Category.Builder builder = ProductProto.Category.newBuilder()
                .setId(category.id())
                .setName(category.name() != null ? category.name() : "")
                .setSlug(category.slug() != null ? category.slug() : "")
                .setDisplayOrder(category.displayOrder() != null ? category.displayOrder() : 0)
                .setIsActive(category.isActive() != null ? category.isActive() : false);
        
        if (category.description() != null) {
            builder.setDescription(category.description());
        }
        if (category.parentId() != null) {
            builder.setParentId(category.parentId());
        }
        if (category.createdAt() != null) {
            Instant instant = category.createdAt().toInstant(java.time.ZoneOffset.UTC);
            builder.setCreatedAt(Timestamp.newBuilder()
                    .setSeconds(instant.getEpochSecond())
                    .setNanos(instant.getNano())
                    .build());
        }
        if (category.updatedAt() != null) {
            Instant instant = category.updatedAt().toInstant(java.time.ZoneOffset.UTC);
            builder.setUpdatedAt(Timestamp.newBuilder()
                    .setSeconds(instant.getEpochSecond())
                    .setNanos(instant.getNano())
                    .build());
        }
        
        return builder.build();
    }
    
    /**
     * Convert proto → domain Product (used when client sends product data to server via gRPC)
     */
    public Product toDomain(ProductProto.Product proto) {
        return new Product(
                proto.getId() == 0 ? null : proto.getId(),
                proto.getTitle().isEmpty() ? null : proto.getTitle(),
                proto.getSlug().isEmpty() ? null : proto.getSlug(),
                proto.getAuthor().isEmpty() ? null : proto.getAuthor(),
                proto.getPublisher().isEmpty() ? null : proto.getPublisher(),
                proto.getPublicationYear() == 0 ? null : proto.getPublicationYear(),
                proto.getLanguage().isEmpty() ? "Vietnamese" : proto.getLanguage(),
                proto.getPages() == 0 ? null : proto.getPages(),
                proto.getFormat().isEmpty() ? "Paperback" : proto.getFormat(),
                proto.getDescription().isEmpty() ? null : proto.getDescription(),
                proto.getPrice() == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(proto.getPrice()),
                proto.getDiscountPrice() == 0 ? null : BigDecimal.valueOf(proto.getDiscountPrice()),
                proto.getStockQuantity(),
                proto.getCoverImageUrl().isEmpty() ? null : proto.getCoverImageUrl(),
                proto.getIsBestseller(),
                proto.getIsActive(),
                proto.getViewCount(),
                proto.getSoldCount(),
                BigDecimal.valueOf(proto.getRatingAverage()),
                proto.getRatingCount(),
                null, // createdAt — managed by server
                null, // updatedAt — managed by server
                null, // deletedAt
                proto.getCategoryId() == 0 ? null : proto.getCategoryId()
        );
    }
    
    /**
     * Convert proto → domain Category (used when client sends category data to server via gRPC)
     */
    public Category toDomain(ProductProto.Category proto) {
        return new Category(
                proto.getId() == 0 ? null : proto.getId(),
                proto.getName().isEmpty() ? null : proto.getName(),
                proto.getSlug().isEmpty() ? null : proto.getSlug(),
                proto.getDescription().isEmpty() ? null : proto.getDescription(),
                proto.getParentId() == 0 ? null : proto.getParentId(),
                proto.getDisplayOrder(),
                proto.getIsActive(),
                null, // createdAt
                null, // updatedAt
                null  // deletedAt
        );
    }
}