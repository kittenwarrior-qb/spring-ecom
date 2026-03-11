package com.example.spring_ecom.repository.database.cart;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.cart.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface CartEntityMapper extends BaseEntityMapper<Cart, CartEntity> {
    
    @Override
    Cart toDomain(CartEntity entity);
    
    @Override
    CartEntity toEntity(Cart domain);
}
