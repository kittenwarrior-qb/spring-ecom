package com.example.spring_ecom.repository.database.order;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.order.CreateOrderFromCartRequest;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderCalculation;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.PaymentMethod;
import com.example.spring_ecom.domain.order.PaymentStatus;
import com.example.spring_ecom.repository.database.coupon.CouponRepository;
import com.example.spring_ecom.repository.database.order.dao.CreateOrderEntityDao;
import com.example.spring_ecom.repository.database.order.dao.CreateOrderFromCartDao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.AfterMapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Objects;

@Mapper(config = MapStructGlobalConfig.class, imports = {Objects.class, BigDecimal.class, OrderStatus.class, PaymentStatus.class})
public abstract class OrderEntityMapper implements BaseEntityMapper<Order, OrderEntity> {
    
    @Autowired
    protected CouponRepository couponRepository;
    
    @Override
    public abstract Order toDomain(OrderEntity entity);
    
    @AfterMapping
    protected void mapCouponCode(OrderEntity entity, @MappingTarget Order.Builder builder) {
        if (entity.getCouponId() != null) {
            couponRepository.findById(entity.getCouponId())
                .ifPresent(coupon -> builder.couponCode(coupon.getCode()));
        }
    }
    
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "status", expression = "java(Objects.nonNull(domain.status()) ? domain.status() : OrderStatus.PENDING_STOCK)")
    @Mapping(target = "paymentStatus", expression = "java(Objects.nonNull(domain.paymentStatus()) ? domain.paymentStatus() : PaymentStatus.UNPAID)")
    @Mapping(target = "shippingFee", expression = "java(Objects.nonNull(domain.shippingFee()) ? domain.shippingFee() : BigDecimal.ZERO)")
    @Mapping(target = "discount", expression = "java(Objects.nonNull(domain.discount()) ? domain.discount() : BigDecimal.ZERO)")
    @Mapping(target = "couponId", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    public abstract OrderEntity toEntity(Order domain);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    public abstract void update(@MappingTarget OrderEntity entity, Order domain);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "status", expression = "java(OrderStatus.PENDING_STOCK)")
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    public abstract OrderEntity toEntityFromCart(CreateOrderEntityDao dao);
    
    @Mapping(target = "subtotal", source = "calculation.subtotal")
    @Mapping(target = "shippingFee", source = "calculation.shippingFee")
    @Mapping(target = "discount", source = "calculation.discount")
    @Mapping(target = "total", source = "calculation.total")
    @Mapping(target = "couponId", source = "calculation.couponId")
    public abstract CreateOrderEntityDao toCreateOrderEntityDao(
        CreateOrderFromCartDao request, 
        OrderCalculation calculation
    );
    
    public abstract CreateOrderFromCartRequest toDomain(CreateOrderFromCartDao dao);
    
    public void updateOrderStatus(@MappingTarget OrderEntity entity, OrderStatus status) {
        entity.setStatus(status);
        if (status == OrderStatus.CANCELLED) {
            entity.setCancelledAt(java.time.LocalDateTime.now());
        }
    }
    
    public void updatePaymentStatus(@MappingTarget OrderEntity entity, PaymentStatus paymentStatus) {
        entity.setPaymentStatus(paymentStatus);
    }
    
    public void cancelOrder(@MappingTarget OrderEntity entity) {
        entity.setStatus(OrderStatus.CANCELLED);
        entity.setCancelledAt(java.time.LocalDateTime.now());
        if (entity.getPaymentStatus() == PaymentStatus.PAID) {
            entity.setPaymentStatus(PaymentStatus.REFUNDED);
        }
    }
    
    public void setInitialPaymentStatus(@MappingTarget OrderEntity entity, PaymentMethod paymentMethod) {
        if (paymentMethod == PaymentMethod.COD) {
            entity.setPaymentStatus(PaymentStatus.UNPAID);
        } else if (paymentMethod == PaymentMethod.BANK_TRANSFER) {
            entity.setPaymentStatus(PaymentStatus.PENDING);
        }
    }
}
