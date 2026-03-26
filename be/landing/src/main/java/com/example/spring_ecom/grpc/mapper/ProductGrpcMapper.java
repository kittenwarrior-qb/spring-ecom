package com.example.spring_ecom.grpc.mapper;

import com.example.spring_ecom.grpc.domain.ProductProto;
import com.example.spring_ecom.controller.api.product.model.ProductRequest;
import com.example.spring_ecom.controller.api.product.model.ProductResponse;
import com.example.spring_ecom.domain.product.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductGrpcMapper {

    public ProductProto.Product toProto(Product product) {
        ProductProto.Product.Builder builder = ProductProto.Product.newBuilder()
                .setTitle(product.title() != null ? product.title() : "")
                .setSlug(product.slug() != null ? product.slug() : "")
                .setAuthor(product.author() != null ? product.author() : "")
                .setLanguage(product.language() != null ? product.language() : "Vietnamese")
                .setFormat(product.format() != null ? product.format() : "Paperback")
                .setPrice(product.price() != null ? product.price().doubleValue() : 0)
                .setStockQuantity(product.stockQuantity() != null ? product.stockQuantity() : 0)
                .setIsBestseller(product.isBestseller() != null ? product.isBestseller() : false)
                .setIsActive(product.isActive() != null ? product.isActive() : true)
                .setViewCount(product.viewCount() != null ? product.viewCount() : 0)
                .setSoldCount(product.soldCount() != null ? product.soldCount() : 0)
                .setRatingAverage(product.ratingAverage() != null ? product.ratingAverage().doubleValue() : 0)
                .setRatingCount(product.ratingCount() != null ? product.ratingCount() : 0);

        if (product.id() != null) {
            builder.setId(product.id());
        }
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

        return builder.build();
    }

    public ProductProto.Product toProto(ProductRequest request) {
        ProductProto.Product.Builder builder = ProductProto.Product.newBuilder()
                .setTitle(request.title() != null ? request.title() : "")
                .setAuthor(request.author() != null ? request.author() : "")
                .setLanguage(request.language() != null ? request.language() : "Vietnamese")
                .setFormat(request.format() != null ? request.format() : "Paperback")
                .setPrice(request.price() != null ? request.price().doubleValue() : 0)
                .setStockQuantity(request.stockQuantity() != null ? request.stockQuantity() : 0)
                .setIsBestseller(request.isBestseller() != null ? request.isBestseller() : false)
                .setIsActive(request.isActive() != null ? request.isActive() : true);

        if (request.slug() != null) {
            builder.setSlug(request.slug());
        }
        if (request.publisher() != null) {
            builder.setPublisher(request.publisher());
        }
        if (request.publicationYear() != null) {
            builder.setPublicationYear(request.publicationYear());
        }
        if (request.pages() != null) {
            builder.setPages(request.pages());
        }
        if (request.description() != null) {
            builder.setDescription(request.description());
        }
        if (request.discountPrice() != null) {
            builder.setDiscountPrice(request.discountPrice().doubleValue());
        }
        if (request.coverImageUrl() != null) {
            builder.setCoverImageUrl(request.coverImageUrl());
        }
        if (request.categoryId() != null) {
            builder.setCategoryId(request.categoryId());
        }

        return builder.build();
    }

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
                null, // createdAt
                null, // updatedAt
                null, // deletedAt
                proto.getCategoryId() == 0 ? null : proto.getCategoryId()
        );
    }
    
    /**
     * Convert ProductProto directly to ProductResponse for demo/testing
     */
    public ProductResponse toResponse(ProductProto.Product proto) {
        return new ProductResponse(
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
                null, // createdAt
                null, // updatedAt
                proto.getCategoryId() == 0 ? null : proto.getCategoryId(),
                null  // categoryName
        );
    }
}
