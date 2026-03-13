package com.example.spring_ecom.controller.api.cart.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.domain.cart.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface UpdateCartItemRequestMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cartId", ignore = true)
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CartItem toDomain(UpdateCartItemRequest request);
}