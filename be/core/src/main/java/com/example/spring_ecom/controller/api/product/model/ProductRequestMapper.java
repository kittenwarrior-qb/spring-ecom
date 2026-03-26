package com.example.spring_ecom.controller.api.product.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseModelMapper;
import com.example.spring_ecom.domain.product.Product;
import org.mapstruct.Mapper;

@Mapper(config = MapStructGlobalConfig.class)
public interface ProductRequestMapper extends BaseModelMapper<ProductRequest, Product> {
}
