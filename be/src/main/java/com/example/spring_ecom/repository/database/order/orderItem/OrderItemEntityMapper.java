package com.example.spring_ecom.repository.database.order.orderItem;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.order.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface OrderItemEntityMapper extends BaseEntityMapper<OrderItem, OrderItemEntity> {
    
    @Override
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "product.id", target = "productId")
    OrderItem toDomain(OrderItemEntity entity);
    
    @Override
    @Mapping(source = "orderId", target = "order.id")
    @Mapping(source = "productId", target = "product.id")
    OrderItemEntity toEntity(OrderItem domain);
}
