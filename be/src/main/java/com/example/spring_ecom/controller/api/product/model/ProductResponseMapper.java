package com.example.spring_ecom.controller.api.product.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseModelMapper;
import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface ProductResponseMapper extends BaseModelMapper<ProductResponse, Product> {
    
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ProductResponse fromEntity(ProductEntity entity);
}
