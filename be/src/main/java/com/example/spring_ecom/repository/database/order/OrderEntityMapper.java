package com.example.spring_ecom.repository.database.order;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.PaymentMethod;
import com.example.spring_ecom.domain.order.PaymentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.util.Objects;

@Mapper(config = MapStructGlobalConfig.class, imports = {Objects.class, BigDecimal.class, OrderStatus.class, PaymentStatus.class})
public interface OrderEntityMapper extends BaseEntityMapper<Order, OrderEntity> {
    
    @Override
    Order toDomain(OrderEntity entity);
    
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "status", expression = "java(Objects.nonNull(domain.status()) ? domain.status() : OrderStatus.PENDING)")
    @Mapping(target = "paymentStatus", expression = "java(Objects.nonNull(domain.paymentStatus()) ? domain.paymentStatus() : PaymentStatus.UNPAID)")
    @Mapping(target = "shippingFee", expression = "java(Objects.nonNull(domain.shippingFee()) ? domain.shippingFee() : BigDecimal.ZERO)")
    @Mapping(target = "discount", expression = "java(Objects.nonNull(domain.discount()) ? domain.discount() : BigDecimal.ZERO)")
    @Mapping(target = "cancelledAt", ignore = true)
    OrderEntity toEntity(Order domain);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    void update(@MappingTarget OrderEntity entity, Order domain);
    
    default void updateOrderStatus(@MappingTarget OrderEntity entity, OrderStatus status) {
        entity.setStatus(status);
        if (status == OrderStatus.CANCELLED) {
            entity.setCancelledAt(java.time.LocalDateTime.now());
        }
    }
    
    default void updatePaymentStatus(@MappingTarget OrderEntity entity, PaymentStatus paymentStatus) {
        entity.setPaymentStatus(paymentStatus);
    }
    
    default void cancelOrder(@MappingTarget OrderEntity entity) {
        entity.setStatus(OrderStatus.CANCELLED);
        entity.setCancelledAt(java.time.LocalDateTime.now());
        if (entity.getPaymentStatus() == PaymentStatus.PAID) {
            entity.setPaymentStatus(PaymentStatus.REFUNDED);
        }
    }
    
    default void setInitialPaymentStatus(@MappingTarget OrderEntity entity, PaymentMethod paymentMethod) {
        if (paymentMethod == PaymentMethod.COD) {
            entity.setPaymentStatus(PaymentStatus.UNPAID);
        } else if (paymentMethod == PaymentMethod.BANK_TRANSFER) {
            entity.setPaymentStatus(PaymentStatus.PENDING);
        }
    }
}
