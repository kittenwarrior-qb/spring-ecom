package com.example.spring_ecom.controller.grpc.product;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.grpc.domain.ProductProto;
import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.domain.category.Category;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.Objects;

@Mapper(config = MapStructGlobalConfig.class, imports = Objects.class)
public interface ProductGrpcMapper {

    // ========== Domain -> Proto ==========

    @BeanMapping(builder = @Builder(buildMethod = "build"))
    @Mapping(target = "id", expression = "java(Objects.nonNull(product.id()) ? product.id() : 0L)")
    @Mapping(target = "title", expression = "java(nullToEmpty(product.title()))")
    @Mapping(target = "slug", expression = "java(nullToEmpty(product.slug()))")
    @Mapping(target = "author", expression = "java(nullToEmpty(product.author()))")
    @Mapping(target = "publisher", expression = "java(nullToEmpty(product.publisher()))")
    @Mapping(target = "publicationYear", expression = "java(Objects.nonNull(product.publicationYear()) ? product.publicationYear() : 0)")
    @Mapping(target = "language", expression = "java(nullToEmpty(product.language()))")
    @Mapping(target = "pages", expression = "java(Objects.nonNull(product.pages()) ? product.pages() : 0)")
    @Mapping(target = "format", expression = "java(nullToEmpty(product.format()))")
    @Mapping(target = "description", expression = "java(nullToEmpty(product.description()))")
    @Mapping(target = "price", expression = "java(bigDecimalToDouble(product.price()))")
    @Mapping(target = "discountPrice", expression = "java(bigDecimalToDouble(product.discountPrice()))")
    @Mapping(target = "stockQuantity", expression = "java(Objects.nonNull(product.stockQuantity()) ? product.stockQuantity() : 0)")
    @Mapping(target = "coverImageUrl", expression = "java(nullToEmpty(product.coverImageUrl()))")
    @Mapping(target = "isBestseller", expression = "java(Objects.nonNull(product.isBestseller()) ? product.isBestseller() : false)")
    @Mapping(target = "isActive", expression = "java(Objects.nonNull(product.isActive()) ? product.isActive() : false)")
    @Mapping(target = "viewCount", expression = "java(Objects.nonNull(product.viewCount()) ? product.viewCount() : 0)")
    @Mapping(target = "soldCount", expression = "java(Objects.nonNull(product.soldCount()) ? product.soldCount() : 0)")
    @Mapping(target = "ratingAverage", expression = "java(bigDecimalToDouble(product.ratingAverage()))")
    @Mapping(target = "ratingCount", expression = "java(Objects.nonNull(product.ratingCount()) ? product.ratingCount() : 0)")
    @Mapping(target = "categoryId", expression = "java(Objects.nonNull(product.categoryId()) ? product.categoryId() : 0L)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductProto.Product toProto(Product product);

    @BeanMapping(builder = @Builder(buildMethod = "build"))
    @Mapping(target = "id", expression = "java(Objects.nonNull(category.id()) ? category.id() : 0L)")
    @Mapping(target = "name", expression = "java(nullToEmpty(category.name()))")
    @Mapping(target = "slug", expression = "java(nullToEmpty(category.slug()))")
    @Mapping(target = "description", expression = "java(nullToEmpty(category.description()))")
    @Mapping(target = "parentId", expression = "java(Objects.nonNull(category.parentId()) ? category.parentId() : 0L)")
    @Mapping(target = "displayOrder", expression = "java(Objects.nonNull(category.displayOrder()) ? category.displayOrder() : 0)")
    @Mapping(target = "isActive", expression = "java(Objects.nonNull(category.isActive()) ? category.isActive() : false)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductProto.Category toProto(Category category);

    // ========== Proto -> Domain ==========

    @Mapping(target = "id", expression = "java(zeroToNullLong(proto.getId()))")
    @Mapping(target = "title", expression = "java(emptyToNull(proto.getTitle()))")
    @Mapping(target = "slug", expression = "java(emptyToNull(proto.getSlug()))")
    @Mapping(target = "author", expression = "java(emptyToNull(proto.getAuthor()))")
    @Mapping(target = "publisher", expression = "java(emptyToNull(proto.getPublisher()))")
    @Mapping(target = "publicationYear", expression = "java(zeroToNullInt(proto.getPublicationYear()))")
    @Mapping(target = "language", expression = "java(emptyToNull(proto.getLanguage()))")
    @Mapping(target = "pages", expression = "java(zeroToNullInt(proto.getPages()))")
    @Mapping(target = "format", expression = "java(emptyToNull(proto.getFormat()))")
    @Mapping(target = "description", expression = "java(emptyToNull(proto.getDescription()))")
    @Mapping(target = "price", expression = "java(doubleToBigDecimal(proto.getPrice()))")
    @Mapping(target = "discountPrice", expression = "java(doubleToBigDecimalOrNull(proto.getDiscountPrice()))")
    @Mapping(target = "costPrice", ignore = true)
    @Mapping(target = "stockQuantity", expression = "java(proto.getStockQuantity())")
    @Mapping(target = "coverImageUrl", expression = "java(emptyToNull(proto.getCoverImageUrl()))")
    @Mapping(target = "isBestseller", expression = "java(proto.getIsBestseller())")
    @Mapping(target = "isActive", expression = "java(proto.getIsActive())")
    @Mapping(target = "viewCount", expression = "java(proto.getViewCount())")
    @Mapping(target = "soldCount", expression = "java(proto.getSoldCount())")
    @Mapping(target = "ratingAverage", expression = "java(doubleToBigDecimal(proto.getRatingAverage()))")
    @Mapping(target = "ratingCount", expression = "java(proto.getRatingCount())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "categoryId", expression = "java(zeroToNullLong(proto.getCategoryId()))")
    Product toDomain(ProductProto.Product proto);
    
    @Mapping(target = "id", expression = "java(zeroToNullLong(proto.getId()))")
    @Mapping(target = "name", expression = "java(emptyToNull(proto.getName()))")
    @Mapping(target = "slug", expression = "java(emptyToNull(proto.getSlug()))")
    @Mapping(target = "description", expression = "java(emptyToNull(proto.getDescription()))")
    @Mapping(target = "parentId", expression = "java(zeroToNullLong(proto.getParentId()))")
    @Mapping(target = "displayOrder", expression = "java(proto.getDisplayOrder())")
    @Mapping(target = "isActive", expression = "java(proto.getIsActive())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Category toDomain(ProductProto.Category proto);

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
        return Objects.isNull(value) || value.isEmpty() ? null : value;
    }
    
    @Named("nullToEmpty")
    default String nullToEmpty(String value) {
        return Objects.isNull(value) ? "" : value;
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
        return Objects.isNull(value) ? 0 : value.doubleValue();
    }
}