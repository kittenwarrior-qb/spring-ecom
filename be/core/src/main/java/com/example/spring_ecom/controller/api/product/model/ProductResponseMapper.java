package com.example.spring_ecom.controller.api.product.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseModelMapper;
import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.domain.product.ProductWithCategory;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface ProductResponseMapper extends BaseModelMapper<ProductResponse, Product> {
    
    @Mapping(target = "categoryName", source = "category.name")
    ProductResponse toResponse(ProductEntity entity);
    
    ProductResponse toResponse(ProductWithCategory domain);
}
