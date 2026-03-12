package com.example.spring_ecom.controller.api.order.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseModelMapper;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.repository.database.user.UserRepository;
import com.example.spring_ecom.service.order.detail.OrderDetailService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(config = MapStructGlobalConfig.class)
public abstract class OrderResponseMapper implements BaseModelMapper<OrderResponse, Order> {
    
    @Autowired
    protected OrderDetailService orderDetailService;
    
    @Autowired
    protected UserRepository userRepository;
    
    @Mapping(target = "items", expression = "java(getOrderItems(order.id()))")
    @Mapping(target = "userEmail", expression = "java(getUserEmail(order.userId()))")
    public abstract OrderResponse toResDto(Order order);
    
    protected java.util.List<OrderItemResponse> getOrderItems(Long orderId) {
        if (orderId == null) return java.util.Collections.emptyList();
        try {
            return orderDetailService.getOrderDetail(orderId)
                    .map(OrderDetailResponse::items)
                    .orElse(java.util.Collections.emptyList());
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }
    
    protected String getUserEmail(Long userId) {
        if (userId == null) return null;
        try {
            return userRepository.findById(userId)
                    .map(user -> user.getEmail())
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
