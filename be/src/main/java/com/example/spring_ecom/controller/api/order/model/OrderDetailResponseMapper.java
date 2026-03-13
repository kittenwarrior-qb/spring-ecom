package com.example.spring_ecom.controller.api.order.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.domain.order.OrderWithUserDto;
import com.example.spring_ecom.domain.order.OrderItemWithProductDto;
import com.example.spring_ecom.service.order.dao.OrderWithUserDao;
import com.example.spring_ecom.service.order.dao.OrderItemWithProductDao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapStructGlobalConfig.class)
public interface OrderDetailResponseMapper {
    
    @Mapping(target = "paymentMethod", expression = "java(com.example.spring_ecom.domain.order.PaymentMethod.valueOf(dao.paymentMethod()))")
    OrderWithUserDto toDto(OrderWithUserDao dao);
    
    OrderItemWithProductDto toDto(OrderItemWithProductDao dao);
    
    @Mapping(source = "productCoverImageUrl", target = "productImage")
    OrderItemResponse toResponse(OrderItemWithProductDto dto);
    
    List<OrderItemWithProductDto> toDtoList(List<OrderItemWithProductDao> daoList);
    
    List<OrderItemResponse> toResponseList(List<OrderItemWithProductDto> dtoList);
    
    @Mapping(source = "order.id", target = "id")
    @Mapping(source = "order.orderNumber", target = "orderNumber")
    @Mapping(source = "order.userId", target = "userId")
    @Mapping(source = "order.userEmail", target = "userEmail")
    @Mapping(source = "order.status", target = "status")
    @Mapping(source = "order.subtotal", target = "subtotal")
    @Mapping(source = "order.shippingFee", target = "shippingFee")
    @Mapping(source = "order.discount", target = "discount")
    @Mapping(source = "order.total", target = "total")
    @Mapping(source = "order.paymentMethod", target = "paymentMethod")
    @Mapping(source = "order.shippingAddress", target = "shippingAddress")
    @Mapping(source = "order.shippingCity", target = "shippingCity")
    @Mapping(source = "order.shippingDistrict", target = "shippingDistrict")
    @Mapping(source = "order.shippingWard", target = "shippingWard")
    @Mapping(source = "order.recipientName", target = "recipientName")
    @Mapping(source = "order.recipientPhone", target = "recipientPhone")
    @Mapping(source = "order.note", target = "note")
    @Mapping(source = "items", target = "items")
    @Mapping(source = "order.createdAt", target = "createdAt")
    @Mapping(source = "order.updatedAt", target = "updatedAt")
    @Mapping(source = "order.cancelledAt", target = "cancelledAt")
    OrderDetailResponse toDetailResponse(OrderWithUserDto order, List<OrderItemResponse> items);
}