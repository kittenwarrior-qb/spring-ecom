package com.example.spring_ecom.controller.api.order.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseModelMapper;
import com.example.spring_ecom.domain.order.Order;
import org.mapstruct.Mapper;

@Mapper(config = MapStructGlobalConfig.class)
public interface OrderResponseMapper extends BaseModelMapper<OrderResponse, Order> {
}
