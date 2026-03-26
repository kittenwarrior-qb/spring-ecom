package com.example.spring_ecom.controller.api.order.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.repository.database.order.dao.CreateOrderFromCartDao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface CreateOrderRequestMapper {
    
    @Mapping(target = "userId", source = "userId")
    CreateOrderFromCartDao toDomain(Long userId, CreateOrderRequest request);
}