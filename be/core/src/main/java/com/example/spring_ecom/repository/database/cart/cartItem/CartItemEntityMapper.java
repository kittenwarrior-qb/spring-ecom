package com.example.spring_ecom.repository.database.cart.cartItem;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.cart.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface CartItemEntityMapper extends BaseEntityMapper<CartItem, CartItemEntity> {
    
    @Override
    CartItem toDomain(CartItemEntity entity);
    
    @Override
    CartItemEntity toEntity(CartItem domain);
}
