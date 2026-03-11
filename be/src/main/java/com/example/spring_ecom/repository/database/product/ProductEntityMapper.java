package com.example.spring_ecom.repository.database.product;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface ProductEntityMapper extends BaseEntityMapper<Product, ProductEntity> {
    
    @Override
    @Mapping(source = "categoryId", target = "category.id")
    ProductEntity toEntity(Product domain);
    
    @Override
    @Mapping(source = "category.id", target = "categoryId")
    Product toDomain(ProductEntity entity);
}
