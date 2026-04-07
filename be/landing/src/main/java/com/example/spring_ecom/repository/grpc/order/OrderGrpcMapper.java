package com.example.spring_ecom.repository.grpc.order;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.grpc.domain.OrderProto;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.PaymentStatus;
import com.example.spring_ecom.domain.order.PaymentMethod;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(config = MapStructGlobalConfig.class)
public interface OrderGrpcMapper {

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

    // ========== Domain -> Proto ==========

    @BeanMapping(builder = @Builder(buildMethod = "build"))
    @Mapping(target = "id", expression = "java(order.id() != null ? order.id() : 0L)")
    @Mapping(target = "orderNumber", expression = "java(nullToEmpty(order.orderNumber()))")
    @Mapping(target = "userId", expression = "java(order.userId() != null ? order.userId() : 0L)")
    @Mapping(target = "status", expression = "java(order.status() != null ? order.status().name() : \"\")")
    @Mapping(target = "paymentMethod", expression = "java(order.paymentMethod() != null ? order.paymentMethod().name() : \"\")")
    @Mapping(target = "paymentStatus", expression = "java(order.paymentStatus() != null ? order.paymentStatus().name() : \"\")")
    @Mapping(target = "subtotal", expression = "java(bigDecimalToDouble(order.subtotal()))")
    @Mapping(target = "shippingFee", expression = "java(bigDecimalToDouble(order.shippingFee()))")
    @Mapping(target = "discount", expression = "java(bigDecimalToDouble(order.discount()))")
    @Mapping(target = "total", expression = "java(bigDecimalToDouble(order.total()))")
    @Mapping(target = "shippingAddress", expression = "java(nullToEmpty(order.shippingAddress()))")
    @Mapping(target = "shippingCity", expression = "java(nullToEmpty(order.shippingCity()))")
    @Mapping(target = "shippingDistrict", expression = "java(nullToEmpty(order.shippingDistrict()))")
    @Mapping(target = "shippingWard", expression = "java(nullToEmpty(order.shippingWard()))")
    @Mapping(target = "recipientName", expression = "java(nullToEmpty(order.recipientName()))")
    @Mapping(target = "recipientPhone", expression = "java(nullToEmpty(order.recipientPhone()))")
    @Mapping(target = "note", expression = "java(nullToEmpty(order.note()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OrderProto.Order toProto(Order order);

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
