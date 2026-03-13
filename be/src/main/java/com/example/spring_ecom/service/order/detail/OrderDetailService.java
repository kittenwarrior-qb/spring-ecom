package com.example.spring_ecom.service.order.detail;

import com.example.spring_ecom.controller.api.order.orderItem.model.OrderDetailResponse;
import com.example.spring_ecom.controller.api.order.orderItem.model.OrderDetailResponseMapper;
import com.example.spring_ecom.controller.api.order.orderItem.model.OrderItemResponse;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.order.OrderItem.OrderItemWithProductDto;
import com.example.spring_ecom.service.order.dao.OrderWithUserDao;
import com.example.spring_ecom.service.order.dao.OrderItemWithProductDao;
import com.example.spring_ecom.repository.database.order.OrderRepository;
import com.example.spring_ecom.repository.database.order.orderItem.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderDetailService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderDetailResponseMapper mapper;
    
    public Optional<OrderDetailResponse> getOrderDetail(Long orderId) {
        OrderWithUserDao orderDao = findOrderWithUserById(orderId);
        List<OrderItemWithProductDao> orderItemDaos = orderItemRepository.findOrderItemsWithProductByOrderId(orderId);
        
        OrderWithUserDao order = orderDao;
        List<OrderItemWithProductDto> orderItems = mapper.toDtoList(orderItemDaos);
        List<OrderItemResponse> items = mapper.toResponseList(orderItems);
        
        OrderDetailResponse response = mapper.toDetailResponse(order, items);
        return Optional.of(response);
    }
    
    private OrderWithUserDao findOrderWithUserById(Long orderId) {
        return orderRepository.findOrderWithUserById(orderId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Order not found"));
    }
}
