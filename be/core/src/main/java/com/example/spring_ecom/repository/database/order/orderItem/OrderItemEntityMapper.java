package com.example.spring_ecom.repository.database.order.orderItem;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.domain.order.OrderItem.OrderItem;
import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.repository.database.order.OrderEntity;
import com.example.spring_ecom.repository.database.product.ProductEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(config = MapStructGlobalConfig.class)
public interface OrderItemEntityMapper extends BaseEntityMapper<OrderItem, OrderItemEntity> {
    
    @Override
    OrderItem toDomain(OrderItemEntity entity);
    
    @Override
    OrderItemEntity toEntity(OrderItem domain);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productTitle", source = "product.title")
    @Mapping(target = "quantity", source = "cartItem.quantity")
    @Mapping(target = "price", source = "cartItem.price")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(cartItem.price(), cartItem.quantity()))")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "cancelledQuantity", constant = "0")
    @Mapping(target = "cancelledAt", ignore = true)
    OrderItemEntity createFromCartItem(OrderEntity order, CartItem cartItem, ProductEntity product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productTitle", source = "product.title")
    @Mapping(target = "quantity", source = "cartItem.quantity")
    @Mapping(target = "price", source = "cartItem.price")
    @Mapping(target = "costPrice", source = "product.costPrice")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(cartItem.price(), cartItem.quantity()))")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "cancelledQuantity", constant = "0")
    @Mapping(target = "cancelledAt", ignore = true)
    OrderItemEntity createFromCartItemDomain(OrderEntity order, CartItem cartItem, Product product);

    default BigDecimal calculateSubtotal(BigDecimal price, Integer quantity) {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
