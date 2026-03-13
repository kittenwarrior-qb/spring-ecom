package com.example.spring_ecom.controller.api.order.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseModelMapper;
import com.example.spring_ecom.domain.order.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class, componentModel = "spring")
public interface OrderResponseMapper extends BaseModelMapper<OrderResponse, Order> {
    
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "userEmail", ignore = true)
    OrderResponse toResponse(Order order);
}
