package com.example.spring_ecom.repository.grpc.product;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.grpc.domain.ProductProto;
import com.example.spring_ecom.controller.api.product.model.ProductRequest;
import com.example.spring_ecom.controller.api.product.model.ProductResponse;
import com.example.spring_ecom.domain.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(config = MapStructGlobalConfig.class)
public interface ProductGrpcMapper {

    // ========== Proto -> Domain ==========
    
    @Mapping(target = "id", expression = "java(zeroToNullLong(proto.getId()))")
    @Mapping(target = "title", expression = "java(emptyToNull(proto.getTitle()))")
    @Mapping(target = "slug", expression = "java(emptyToNull(proto.getSlug()))")
    @Mapping(target = "author", expression = "java(emptyToNull(proto.getAuthor()))")
    @Mapping(target = "publisher", expression = "java(emptyToNull(proto.getPublisher()))")
    @Mapping(target = "publicationYear", expression = "java(zeroToNullInt(proto.getPublicationYear()))")
    @Mapping(target = "language", defaultValue = "Vietnamese")
    @Mapping(target = "pages", expression = "java(zeroToNullInt(proto.getPages()))")
    @Mapping(target = "format", defaultValue = "Paperback")
    @Mapping(target = "description", expression = "java(emptyToNull(proto.getDescription()))")
    @Mapping(target = "price", expression = "java(doubleToBigDecimal(proto.getPrice()))")
    @Mapping(target = "discountPrice", expression = "java(doubleToBigDecimalOrNull(proto.getDiscountPrice()))")
    @Mapping(target = "coverImageUrl", expression = "java(emptyToNull(proto.getCoverImageUrl()))")
    @Mapping(target = "categoryId", expression = "java(zeroToNullLong(proto.getCategoryId()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Product toDomain(ProductProto.Product proto);

    // ========== Proto -> Response ==========
    
    @Mapping(target = "id", expression = "java(zeroToNullLong(proto.getId()))")
    @Mapping(target = "title", expression = "java(emptyToNull(proto.getTitle()))")
    @Mapping(target = "slug", expression = "java(emptyToNull(proto.getSlug()))")
    @Mapping(target = "author", expression = "java(emptyToNull(proto.getAuthor()))")
    @Mapping(target = "publisher", expression = "java(emptyToNull(proto.getPublisher()))")
    @Mapping(target = "publicationYear", expression = "java(zeroToNullInt(proto.getPublicationYear()))")
    @Mapping(target = "language", defaultValue = "Vietnamese")
    @Mapping(target = "pages", expression = "java(zeroToNullInt(proto.getPages()))")
    @Mapping(target = "format", defaultValue = "Paperback")
    @Mapping(target = "description", expression = "java(emptyToNull(proto.getDescription()))")
    @Mapping(target = "price", expression = "java(doubleToBigDecimal(proto.getPrice()))")
    @Mapping(target = "discountPrice", expression = "java(doubleToBigDecimalOrNull(proto.getDiscountPrice()))")
    @Mapping(target = "coverImageUrl", expression = "java(emptyToNull(proto.getCoverImageUrl()))")
    @Mapping(target = "categoryId", expression = "java(zeroToNullLong(proto.getCategoryId()))")
    @Mapping(target = "categoryName", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductResponse toResponse(ProductProto.Product proto);

    // ========== Domain -> Proto ==========
    // Proto uses builder pattern, use default method with manual builder
    
    default ProductProto.Product toProto(Product product) {
        if (product == null) return null;
        
        ProductProto.Product.Builder builder = ProductProto.Product.newBuilder()
                .setId(product.id() != null ? product.id() : 0L)
                .setTitle(nullToEmpty(product.title()))
                .setSlug(nullToEmpty(product.slug()))
                .setAuthor(nullToEmpty(product.author()))
                .setPublisher(nullToEmpty(product.publisher()))
                .setPublicationYear(product.publicationYear() != null ? product.publicationYear() : 0)
                .setLanguage(product.language() != null ? product.language() : "Vietnamese")
                .setPages(product.pages() != null ? product.pages() : 0)
                .setFormat(product.format() != null ? product.format() : "Paperback")
                .setDescription(nullToEmpty(product.description()))
                .setPrice(bigDecimalToDouble(product.price()))
                .setDiscountPrice(product.discountPrice() != null ? product.discountPrice().doubleValue() : 0.0)
                .setStockQuantity(product.stockQuantity() != null ? product.stockQuantity() : 0)
                .setCoverImageUrl(nullToEmpty(product.coverImageUrl()))
                .setIsBestseller(product.isBestseller() != null ? product.isBestseller() : false)
                .setIsActive(product.isActive() != null ? product.isActive() : true)
                .setViewCount(product.viewCount() != null ? product.viewCount() : 0)
                .setSoldCount(product.soldCount() != null ? product.soldCount() : 0)
                .setRatingAverage(bigDecimalToDouble(product.ratingAverage()))
                .setRatingCount(product.ratingCount() != null ? product.ratingCount() : 0)
                .setCategoryId(product.categoryId() != null ? product.categoryId() : 0L);

        return builder.build();
    }

    // ========== Request -> Proto ==========
    
    default ProductProto.Product toProto(ProductRequest request) {
        if (request == null) return null;
        
        ProductProto.Product.Builder builder = ProductProto.Product.newBuilder()
                .setId(0L)
                .setTitle(nullToEmpty(request.title()))
                .setSlug(nullToEmpty(request.slug()))
                .setAuthor(nullToEmpty(request.author()))
                .setPublisher(nullToEmpty(request.publisher()))
                .setPublicationYear(request.publicationYear() != null ? request.publicationYear() : 0)
                .setLanguage(request.language() != null ? request.language() : "Vietnamese")
                .setPages(request.pages() != null ? request.pages() : 0)
                .setFormat(request.format() != null ? request.format() : "Paperback")
                .setDescription(nullToEmpty(request.description()))
                .setPrice(bigDecimalToDouble(request.price()))
                .setDiscountPrice(request.discountPrice() != null ? request.discountPrice().doubleValue() : 0.0)
                .setStockQuantity(request.stockQuantity() != null ? request.stockQuantity() : 0)
                .setCoverImageUrl(nullToEmpty(request.coverImageUrl()))
                .setIsBestseller(request.isBestseller() != null ? request.isBestseller() : false)
                .setIsActive(request.isActive() != null ? request.isActive() : true)
                .setViewCount(0)
                .setSoldCount(0)
                .setRatingAverage(0.0)
                .setRatingCount(0)
                .setCategoryId(request.categoryId() != null ? request.categoryId() : 0L);

        return builder.build();
    }

    // ========== Helper methods ==========
    
    @Named("zeroToNullLong")
    default Long zeroToNullLong(long value) {
        return value == 0 ? null : value;
    }
    
    @Named("zeroToNullInt")
    default Integer zeroToNullInt(int value) {
        return value == 0 ? null : value;
    }
    
    @Named("emptyToNull")
    default String emptyToNull(String value) {
        return value == null || value.isEmpty() ? null : value;
    }
    
    @Named("nullToEmpty")
    default String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
    
    @Named("doubleToBigDecimal")
    default BigDecimal doubleToBigDecimal(double value) {
        return value == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(value);
    }
    
    @Named("doubleToBigDecimalOrNull")
    default BigDecimal doubleToBigDecimalOrNull(double value) {
        return value == 0 ? null : BigDecimal.valueOf(value);
    }
    
    @Named("bigDecimalToDouble")
    default double bigDecimalToDouble(BigDecimal value) {
        return value == null ? 0 : value.doubleValue();
    }
}
