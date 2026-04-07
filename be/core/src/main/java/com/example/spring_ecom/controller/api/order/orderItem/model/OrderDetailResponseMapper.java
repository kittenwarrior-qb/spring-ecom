package com.example.spring_ecom.controller.api.order.orderItem.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.config.MinioConfig;
import com.example.spring_ecom.domain.order.OrderItem.OrderItemWithProductDto;
import com.example.spring_ecom.repository.database.order.dao.OrderItemWithProductDao;
import com.example.spring_ecom.repository.database.order.dao.OrderWithUserDao;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

@Mapper(config = MapStructGlobalConfig.class)
public abstract class OrderDetailResponseMapper {
    
    @Autowired
    protected MinioConfig minioConfig;
    
    public abstract OrderItemWithProductDto toDto(OrderItemWithProductDao dao);
    
    @Mapping(target = "productImage", source = "productCoverImageUrl", qualifiedByName = "toFullUrl")
    public abstract OrderItemResponse toResponse(OrderItemWithProductDto dto);
    
    public abstract List<OrderItemWithProductDto> toDtoList(List<OrderItemWithProductDao> daoList);
    
    public abstract List<OrderItemResponse> toResponseList(List<OrderItemWithProductDto> dtoList);
    
    @Mapping(source = "order.id", target = "id")
    @Mapping(source = "order.orderNumber", target = "orderNumber")
    @Mapping(source = "order.userId", target = "userId")
    @Mapping(source = "order.userEmail", target = "userEmail")
    @Mapping(source = "order.status", target = "status")
    @Mapping(source = "order.paymentStatus", target = "paymentStatus")
    @Mapping(source = "order.subtotal", target = "subtotal")
    @Mapping(source = "order.shippingFee", target = "shippingFee")
    @Mapping(source = "order.discount", target = "discount")
    @Mapping(source = "order.total", target = "total")
    @Mapping(source = "order.refundedAmount", target = "refundedAmount")
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
    public abstract OrderDetailResponse toDetailResponse(OrderWithUserDao order, List<OrderItemResponse> items);
    
    @Named("toFullUrl")
    protected String toFullUrl(String filename) {
        if (Objects.isNull(filename) || filename.isBlank()) {
            return null;
        }
        // Already a full URL
        if (filename.startsWith("http://") || filename.startsWith("https://")) {
            return filename;
        }
        // Convert filename to full URL
        return minioConfig.getEndpoint() + "/" + minioConfig.getBucket() + "/" + filename;
    }
}