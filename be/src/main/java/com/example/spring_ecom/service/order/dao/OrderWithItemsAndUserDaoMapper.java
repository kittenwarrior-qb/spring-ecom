package com.example.spring_ecom.service.order.dao;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.controller.api.order.model.OrderResponse;
import com.example.spring_ecom.domain.order.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class, componentModel = "spring")
public interface OrderWithItemsAndUserDaoMapper {
    
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "userEmail", ignore = true)
    OrderWithItemsAndUserDao fromOrder(Order order);
    
    OrderResponse toOrderResponse(OrderWithItemsAndUserDao dao);
}