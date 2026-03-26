package com.example.spring_ecom.controller.grpc;

import com.example.spring_ecom.grpc.domain.OrderProto;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderItem.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderGrpcMapper {
    
    public OrderProto.Order toProto(Order order) {
        OrderProto.Order.Builder builder = OrderProto.Order.newBuilder()
                .setId(order.id())
                .setOrderNumber(order.orderNumber() != null ? order.orderNumber() : "")
                .setUserId(order.userId())
                .setStatus(order.status().name())
                .setPaymentStatus(order.paymentStatus().name())
                .setPaymentMethod(order.paymentMethod().name())
                .setSubtotal(order.subtotal().doubleValue())
                .setShippingFee(order.shippingFee().doubleValue())
                .setTotal(order.total().doubleValue());
        
        // Add optional fields
        if (order.shippingAddress() != null) {
            builder.setShippingAddress(order.shippingAddress());
        }
        if (order.recipientPhone() != null) {
            builder.setRecipientPhone(order.recipientPhone());
        }
        if (order.note() != null) {
            builder.setNote(order.note());
        }
        
        return builder.build();
    }
    
    public OrderProto.OrderItem toProto(OrderItem orderItem) {
        return OrderProto.OrderItem.newBuilder()
                .setId(orderItem.id())
                .setOrderId(orderItem.orderId())
                .setProductId(orderItem.productId())
                .setProductTitle(orderItem.productTitle())
                .setPrice(orderItem.price().doubleValue())
                .setQuantity(orderItem.quantity())
                .setSubtotal(orderItem.subtotal().doubleValue())
                .setStatus(orderItem.status() != null ? orderItem.status().name() : "")
                .build();
    }
    
    public Order toDomain(OrderProto.Order orderProto) {
        return new Order(
                orderProto.getId(),
                orderProto.getOrderNumber(),
                orderProto.getUserId(),
                com.example.spring_ecom.domain.order.OrderStatus.valueOf(orderProto.getStatus()),
                com.example.spring_ecom.domain.order.PaymentStatus.valueOf(orderProto.getPaymentStatus()),
                BigDecimal.valueOf(orderProto.getSubtotal()),
                BigDecimal.valueOf(orderProto.getShippingFee()),
                BigDecimal.valueOf(orderProto.getDiscount()),
                BigDecimal.valueOf(orderProto.getTotal()),
                BigDecimal.ZERO, // refundedAmount - default to 0 for proto conversion
                com.example.spring_ecom.domain.order.PaymentMethod.valueOf(orderProto.getPaymentMethod()),
                orderProto.getShippingAddress(),
                orderProto.getShippingCity(),
                orderProto.getShippingDistrict(),
                orderProto.getShippingWard(),
                orderProto.getRecipientName(),
                orderProto.getRecipientPhone(),
                orderProto.getNote(),
                null, // createdAt - handle Timestamp conversion separately if needed
                null, // updatedAt
                null  // cancelledAt
        );
    }
}