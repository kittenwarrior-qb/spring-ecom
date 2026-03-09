package com.example.spring_ecom.repository.database.order;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.order.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface OrderEntityMapper extends BaseEntityMapper<Order, OrderEntity> {
    
    @Override
    @Mapping(source = "user.id", target = "userId")
    Order toDomain(OrderEntity entity);
    
    @Override
    @Mapping(source = "userId", target = "user.id")
    OrderEntity toEntity(Order domain);
}
