package com.example.spring_ecom.service.order;

import com.example.spring_ecom.controller.api.order.orderItem.model.OrderDetailResponse;
import com.example.spring_ecom.controller.api.order.orderItem.model.OrderDetailResponseMapper;
import com.example.spring_ecom.controller.api.order.orderItem.model.OrderItemResponse;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.OrderItem.OrderItemWithProductDto;
import com.example.spring_ecom.repository.database.order.OrderRepository;
import com.example.spring_ecom.repository.database.order.dao.OrderItemWithProductDao;
import com.example.spring_ecom.repository.database.order.dao.OrderWithUserDao;
import com.example.spring_ecom.repository.database.order.OrderEntityMapper;
import com.example.spring_ecom.service.order.orderItem.OrderItemUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderQueryService {
    
    private final OrderRepository orderRepository;
    private final OrderItemUseCase orderItemUseCase;
    private final OrderDetailResponseMapper mapper;
    private final OrderEntityMapper orderEntityMapper;
    
    // ========== MAIN QUERY METHODS ==========
    
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id)
                .map(orderEntityMapper::toDomain);
    }
    
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(orderEntityMapper::toDomain);
    }
    
    public Page<Order> findByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(orderEntityMapper::toDomain);
    }
    
    public Page<Order> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable) {
        return orderRepository.findByUserIdAndStatus(userId, status, pageable)
                .map(orderEntityMapper::toDomain);
    }
    
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderEntityMapper::toDomain);
    }
    
    public Optional<OrderDetailResponse> getOrderDetail(Long orderId) {
        OrderWithUserDao orderDao = findOrderWithUserById(orderId);
        List<OrderItemWithProductDao> orderItemDaos = orderItemUseCase.findOrderItemsWithProductByOrderId(orderId);
        
        List<OrderItemWithProductDto> orderItems = mapper.toDtoList(orderItemDaos);
        List<OrderItemResponse> items = mapper.toResponseList(orderItems);
        
        OrderDetailResponse response = mapper.toDetailResponse(orderDao, items);
        return Optional.of(response);
    }
    
    // ========== HELPER METHODS ==========
    
    private OrderWithUserDao findOrderWithUserById(Long orderId) {
        return orderRepository.findOrderWithUserById(orderId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Order not found"));
    }
}