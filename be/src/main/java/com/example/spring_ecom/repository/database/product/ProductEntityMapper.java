package com.example.spring_ecom.repository.database.product;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.product.Product;
import org.mapstruct.Mapper;

@Mapper(config = MapStructGlobalConfig.class)
public interface ProductEntityMapper extends BaseEntityMapper<Product, ProductEntity> {
}
