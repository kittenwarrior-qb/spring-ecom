package com.example.spring_ecom.repository.database.product;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.domain.product.ProductWithCategory;
import com.example.spring_ecom.repository.database.product.dao.ProductWithCategoryDao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructGlobalConfig.class)
public interface ProductEntityMapper extends BaseEntityMapper<Product, ProductEntity> {
    
    @Override
    @Mapping(target = "language", defaultValue = "Vietnamese")
    @Mapping(target = "format", defaultValue = "Paperback")
    @Mapping(target = "stockQuantity", defaultValue = "0")
    @Mapping(target = "isBestseller", defaultValue = "false")
    @Mapping(target = "isActive", defaultValue = "true")
    @Mapping(target = "viewCount", defaultValue = "0")
    @Mapping(target = "soldCount", defaultValue = "0")
    @Mapping(target = "ratingAverage", defaultValue = "0")
    @Mapping(target = "ratingCount", defaultValue = "0")
    ProductEntity toEntity(Product domain);
    
    @Override
    Product toDomain(ProductEntity entity);
    
    ProductWithCategory toDomain(ProductWithCategoryDao dao);
    
    @Mapping(target = "deletedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "publisher", ignore = true)
    @Mapping(target = "publicationYear", ignore = true)
    @Mapping(target = "language", ignore = true)
    @Mapping(target = "pages", ignore = true)
    @Mapping(target = "format", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "discountPrice", ignore = true)
    @Mapping(target = "stockQuantity", ignore = true)
    @Mapping(target = "coverImageUrl", ignore = true)
    @Mapping(target = "isBestseller", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "soldCount", ignore = true)
    @Mapping(target = "ratingAverage", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void markAsDeleted(@MappingTarget ProductEntity entity, Product ignored);
    
    @Mapping(target = "slug", source = "slug")
    void updateSlug(@MappingTarget ProductEntity entity, String slug);
}
