package com.example.spring_ecom.repository.database.order;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.PaymentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface OrderEntityMapper extends BaseEntityMapper<Order, OrderEntity> {
    
    @Override
    Order toDomain(OrderEntity entity);
    
    @Override
    @Mapping(target = "status", defaultValue = "PENDING")
    @Mapping(target = "paymentStatus", defaultValue = "UNPAID")
    @Mapping(target = "shippingFee", defaultValue = "0")
    @Mapping(target = "discount", defaultValue = "0")
    OrderEntity toEntity(Order domain);
}
