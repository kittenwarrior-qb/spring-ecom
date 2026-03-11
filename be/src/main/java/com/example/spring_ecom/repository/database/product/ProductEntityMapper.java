package com.example.spring_ecom.repository.database.product;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
}
