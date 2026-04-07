package com.example.spring_ecom.repository.grpc.product;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.grpc.domain.ProductProto;
import com.example.spring_ecom.controller.api.product.model.ProductRequest;
import com.example.spring_ecom.controller.api.product.model.ProductResponse;
import com.example.spring_ecom.domain.product.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
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

    @BeanMapping(builder = @Builder(buildMethod = "build"))
    @Mapping(target = "id", expression = "java(product.id() != null ? product.id() : 0L)")
    @Mapping(target = "title", expression = "java(nullToEmpty(product.title()))")
    @Mapping(target = "slug", expression = "java(nullToEmpty(product.slug()))")
    @Mapping(target = "author", expression = "java(nullToEmpty(product.author()))")
    @Mapping(target = "publisher", expression = "java(nullToEmpty(product.publisher()))")
    @Mapping(target = "publicationYear", expression = "java(product.publicationYear() != null ? product.publicationYear() : 0)")
    @Mapping(target = "language", expression = "java(product.language() != null ? product.language() : \"Vietnamese\")")
    @Mapping(target = "pages", expression = "java(product.pages() != null ? product.pages() : 0)")
    @Mapping(target = "format", expression = "java(product.format() != null ? product.format() : \"Paperback\")")
    @Mapping(target = "description", expression = "java(nullToEmpty(product.description()))")
    @Mapping(target = "price", expression = "java(bigDecimalToDouble(product.price()))")
    @Mapping(target = "discountPrice", expression = "java(product.discountPrice() != null ? product.discountPrice().doubleValue() : 0.0)")
    @Mapping(target = "stockQuantity", expression = "java(product.stockQuantity() != null ? product.stockQuantity() : 0)")
    @Mapping(target = "coverImageUrl", expression = "java(nullToEmpty(product.coverImageUrl()))")
    @Mapping(target = "isBestseller", expression = "java(product.isBestseller() != null ? product.isBestseller() : false)")
    @Mapping(target = "isActive", expression = "java(product.isActive() != null ? product.isActive() : true)")
    @Mapping(target = "viewCount", expression = "java(product.viewCount() != null ? product.viewCount() : 0)")
    @Mapping(target = "soldCount", expression = "java(product.soldCount() != null ? product.soldCount() : 0)")
    @Mapping(target = "ratingAverage", expression = "java(bigDecimalToDouble(product.ratingAverage()))")
    @Mapping(target = "ratingCount", expression = "java(product.ratingCount() != null ? product.ratingCount() : 0)")
    @Mapping(target = "categoryId", expression = "java(product.categoryId() != null ? product.categoryId() : 0L)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductProto.Product toProto(Product product);

    // ========== Request -> Proto ==========

    @BeanMapping(builder = @Builder(buildMethod = "build"))
    @Mapping(target = "id", constant = "0L")
    @Mapping(target = "title", expression = "java(nullToEmpty(request.title()))")
    @Mapping(target = "slug", expression = "java(nullToEmpty(request.slug()))")
    @Mapping(target = "author", expression = "java(nullToEmpty(request.author()))")
    @Mapping(target = "publisher", expression = "java(nullToEmpty(request.publisher()))")
    @Mapping(target = "publicationYear", expression = "java(request.publicationYear() != null ? request.publicationYear() : 0)")
    @Mapping(target = "language", expression = "java(request.language() != null ? request.language() : \"Vietnamese\")")
    @Mapping(target = "pages", expression = "java(request.pages() != null ? request.pages() : 0)")
    @Mapping(target = "format", expression = "java(request.format() != null ? request.format() : \"Paperback\")")
    @Mapping(target = "description", expression = "java(nullToEmpty(request.description()))")
    @Mapping(target = "price", expression = "java(bigDecimalToDouble(request.price()))")
    @Mapping(target = "discountPrice", expression = "java(request.discountPrice() != null ? request.discountPrice().doubleValue() : 0.0)")
    @Mapping(target = "stockQuantity", expression = "java(request.stockQuantity() != null ? request.stockQuantity() : 0)")
    @Mapping(target = "coverImageUrl", expression = "java(nullToEmpty(request.coverImageUrl()))")
    @Mapping(target = "isBestseller", expression = "java(request.isBestseller() != null ? request.isBestseller() : false)")
    @Mapping(target = "isActive", expression = "java(request.isActive() != null ? request.isActive() : true)")
    @Mapping(target = "viewCount", constant = "0")
    @Mapping(target = "soldCount", constant = "0")
    @Mapping(target = "ratingAverage", constant = "0.0")
    @Mapping(target = "ratingCount", constant = "0")
    @Mapping(target = "categoryId", expression = "java(request.categoryId() != null ? request.categoryId() : 0L)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductProto.Product toProto(ProductRequest request);

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
