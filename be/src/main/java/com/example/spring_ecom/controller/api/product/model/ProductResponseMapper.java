package com.example.spring_ecom.controller.api.product.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseModelMapper;
import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(config = MapStructGlobalConfig.class)
public interface ProductResponseMapper extends BaseModelMapper<ProductResponse, Product> {
    
    ProductResponse toResponse(ProductEntity entity);
}
