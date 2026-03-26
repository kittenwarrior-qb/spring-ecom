package com.example.spring_ecom.controller.api.product.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.domain.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

/**
 * Mapper cho admin product request DTOs → domain Product
 *
 * Vì Product là record (immutable), không thể dùng @MappingTarget để partial-update.
 * Thay vào đó:
 *  - toDomain(CreateProductRequest) → Product mới từ create request
 *  - toDomain(UpdateProductRequest, Product) → Product mới merge update + existing
 *    (NullValuePropertyMappingStrategy.IGNORE kế thừa từ MapStructGlobalConfig)
 */
@Mapper(config = MapStructGlobalConfig.class)
public interface AdminProductRequestMapper {

    /**
     * Tạo Product domain mới từ CreateProductRequest.
     * Các field không có trong request được set về giá trị mặc định hoặc ignore.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "viewCount", expression = "java(0)")
    @Mapping(target = "soldCount", expression = "java(0)")
    @Mapping(target = "ratingAverage", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "ratingCount", expression = "java(0)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Product toDomain(CreateProductRequest request);

    /**
     * Tạo Product domain mới từ UpdateProductRequest, lấy các field còn lại từ existing Product.
     * NullValuePropertyMappingStrategy.IGNORE → null field trong request giữ nguyên existing value.
     *
     * MapStruct sẽ generate code như:
     *   title = (request.getTitle() != null) ? request.getTitle() : existing.title()
     */
    @Mapping(target = "id", source = "existing.id")
    @Mapping(target = "slug", source = "existing.slug")
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "author", source = "request.author")
    @Mapping(target = "publisher", source = "request.publisher")
    @Mapping(target = "publicationYear", source = "request.publicationYear")
    @Mapping(target = "language", source = "request.language")
    @Mapping(target = "pages", source = "request.pages")
    @Mapping(target = "format", source = "request.format")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "price", source = "request.price")
    @Mapping(target = "discountPrice", source = "request.discountPrice")
    @Mapping(target = "stockQuantity", source = "request.stockQuantity")
    @Mapping(target = "coverImageUrl", source = "request.coverImageUrl")
    @Mapping(target = "isBestseller", source = "request.isBestseller")
    @Mapping(target = "isActive", source = "request.isActive")
    @Mapping(target = "categoryId", source = "request.categoryId")
    @Mapping(target = "viewCount", source = "existing.viewCount")
    @Mapping(target = "soldCount", source = "existing.soldCount")
    @Mapping(target = "ratingAverage", source = "existing.ratingAverage")
    @Mapping(target = "ratingCount", source = "existing.ratingCount")
    @Mapping(target = "createdAt", source = "existing.createdAt")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", source = "existing.deletedAt")
    Product mergeWithExisting(UpdateProductRequest request, Product existing);
}
