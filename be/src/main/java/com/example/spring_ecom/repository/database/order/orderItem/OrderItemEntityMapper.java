package com.example.spring_ecom.repository.database.order.orderItem;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.order.OrderItem.OrderItem;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface OrderItemEntityMapper extends BaseEntityMapper<OrderItem, OrderItemEntity> {
    
    @Override
    OrderItem toDomain(OrderItemEntity entity);
    
    @Override
    OrderItemEntity toEntity(OrderItem domain);
}
