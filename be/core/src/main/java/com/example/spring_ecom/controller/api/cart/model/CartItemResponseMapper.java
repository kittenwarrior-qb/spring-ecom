package com.example.spring_ecom.controller.api.cart.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseModelMapper;
import com.example.spring_ecom.domain.cart.CartItem;
import org.mapstruct.Mapper;

@Mapper(config = MapStructGlobalConfig.class)
public interface CartItemResponseMapper extends BaseModelMapper<CartItemResponse, CartItem> {
}
