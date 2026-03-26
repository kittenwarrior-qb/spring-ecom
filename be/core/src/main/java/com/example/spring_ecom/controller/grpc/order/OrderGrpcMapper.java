package com.example.spring_ecom.controller.grpc.order;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.grpc.domain.OrderProto;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderItem.OrderItem;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.PaymentStatus;
import com.example.spring_ecom.domain.order.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(config = MapStructGlobalConfig.class)
public interface OrderGrpcMapper {

    // ========== Domain -> Proto ==========
    // Proto uses builder pattern, use default method with manual builder
    
    default OrderProto.Order toProto(Order order) {
        if (order == null) return null;
        
        return OrderProto.Order.newBuilder()
                .setId(order.id() != null ? order.id() : 0L)
                .setOrderNumber(nullToEmpty(order.orderNumber()))
                .setUserId(order.userId() != null ? order.userId() : 0L)
                .setStatus(order.status() != null ? order.status().name() : "")
                .setPaymentStatus(order.paymentStatus() != null ? order.paymentStatus().name() : "")
                .setPaymentMethod(order.paymentMethod() != null ? order.paymentMethod().name() : "")
                .setSubtotal(bigDecimalToDouble(order.subtotal()))
                .setShippingFee(bigDecimalToDouble(order.shippingFee()))
                .setDiscount(bigDecimalToDouble(order.discount()))
                .setTotal(bigDecimalToDouble(order.total()))
                .setShippingAddress(nullToEmpty(order.shippingAddress()))
                .setShippingCity(nullToEmpty(order.shippingCity()))
                .setShippingDistrict(nullToEmpty(order.shippingDistrict()))
                .setShippingWard(nullToEmpty(order.shippingWard()))
                .setRecipientName(nullToEmpty(order.recipientName()))
                .setRecipientPhone(nullToEmpty(order.recipientPhone()))
                .setNote(nullToEmpty(order.note()))
                .build();
    }
    
    default OrderProto.OrderItem toProto(OrderItem orderItem) {
        if (orderItem == null) return null;
        
        return OrderProto.OrderItem.newBuilder()
                .setId(orderItem.id() != null ? orderItem.id() : 0L)
                .setOrderId(orderItem.orderId() != null ? orderItem.orderId() : 0L)
                .setProductId(orderItem.productId() != null ? orderItem.productId() : 0L)
                .setProductTitle(nullToEmpty(orderItem.productTitle()))
                .setPrice(bigDecimalToDouble(orderItem.price()))
                .setQuantity(orderItem.quantity() != null ? orderItem.quantity() : 0)
                .setSubtotal(bigDecimalToDouble(orderItem.subtotal()))
                .setStatus(orderItem.status() != null ? orderItem.status().name() : "")
                .build();
    }

    // ========== Proto -> Domain ==========
    
    @Mapping(target = "id", expression = "java(zeroToNullLong(proto.getId()))")
    @Mapping(target = "orderNumber", expression = "java(emptyToNull(proto.getOrderNumber()))")
    @Mapping(target = "userId", expression = "java(zeroToNullLong(proto.getUserId()))")
    @Mapping(target = "status", expression = "java(toOrderStatus(proto.getStatus()))")
    @Mapping(target = "paymentStatus", expression = "java(toPaymentStatus(proto.getPaymentStatus()))")
    @Mapping(target = "subtotal", expression = "java(doubleToBigDecimal(proto.getSubtotal()))")
    @Mapping(target = "shippingFee", expression = "java(doubleToBigDecimal(proto.getShippingFee()))")
    @Mapping(target = "discount", expression = "java(doubleToBigDecimal(proto.getDiscount()))")
    @Mapping(target = "total", expression = "java(doubleToBigDecimal(proto.getTotal()))")
    @Mapping(target = "paymentMethod", expression = "java(toPaymentMethod(proto.getPaymentMethod()))")
    @Mapping(target = "shippingAddress", expression = "java(emptyToNull(proto.getShippingAddress()))")
    @Mapping(target = "shippingCity", expression = "java(emptyToNull(proto.getShippingCity()))")
    @Mapping(target = "shippingDistrict", expression = "java(emptyToNull(proto.getShippingDistrict()))")
    @Mapping(target = "shippingWard", expression = "java(emptyToNull(proto.getShippingWard()))")
    @Mapping(target = "recipientName", expression = "java(emptyToNull(proto.getRecipientName()))")
    @Mapping(target = "recipientPhone", expression = "java(emptyToNull(proto.getRecipientPhone()))")
    @Mapping(target = "note", expression = "java(emptyToNull(proto.getNote()))")
    @Mapping(target = "couponId", ignore = true)
    @Mapping(target = "couponCode", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    Order toDomain(OrderProto.Order proto);

    // ========== Helper methods ==========
    
    @Named("zeroToNullLong")
    default Long zeroToNullLong(long value) {
        return value == 0 ? null : value;
    }
    
    @Named("emptyToNull")
    default String emptyToNull(String value) {
        return value == null || value.isEmpty() ? null : value;
    }
    
    @Named("nullToEmpty")
    default String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
    
    @Named("doubleToBigDecimal")
    default BigDecimal doubleToBigDecimal(double value) {
        return value == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(value);
    }
    
    @Named("bigDecimalToDouble")
    default double bigDecimalToDouble(BigDecimal value) {
        return value == null ? 0 : value.doubleValue();
    }
    
    @Named("toOrderStatus")
    default OrderStatus toOrderStatus(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return OrderStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    @Named("toPaymentStatus")
    default PaymentStatus toPaymentStatus(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return PaymentStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    @Named("toPaymentMethod")
    default PaymentMethod toPaymentMethod(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return PaymentMethod.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}