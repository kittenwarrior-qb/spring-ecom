package com.example.spring_ecom.controller.api.order.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseModelMapper;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.PaymentMethod;
import com.example.spring_ecom.repository.database.order.dao.OrderWithUserDao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class, componentModel = "spring")
public interface OrderResponseMapper extends BaseModelMapper<OrderResponse, Order> {
    
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "userEmail", ignore = true)
    OrderResponse toResponse(Order order);
    
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "paymentMethod", expression = "java(PaymentMethod.valueOf(orderDao.paymentMethod()))")
    @Mapping(target = "paymentStatus", source = "paymentStatus")
    @Mapping(target = "refundedAmount", source = "refundedAmount")
    OrderResponse toResponse(OrderWithUserDao orderDao);
}
