package com.example.spring_ecom.repository.database.cart;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.cart.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface CartEntityMapper extends BaseEntityMapper<Cart, CartEntity> {
    
    @Override
    @Mapping(source = "user.id", target = "userId")
    Cart toDomain(CartEntity entity);
    
    @Override
    @Mapping(source = "userId", target = "user.id")
    CartEntity toEntity(Cart domain);
}
