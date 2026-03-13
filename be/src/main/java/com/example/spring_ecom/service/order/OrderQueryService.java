package com.example.spring_ecom.service.order;

import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.repository.database.order.OrderEntityMapper;
import com.example.spring_ecom.repository.database.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderQueryService {
    
    private final OrderRepository orderRepository;
    private final OrderEntityMapper mapper;
    
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(mapper::toDomain);
    }
    
    public Page<Order> findByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(mapper::toDomain);
    }
    
    public Page<Order> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable) {
        return orderRepository.findByUserIdAndStatus(userId, status, pageable)
                .map(mapper::toDomain);
    }
    
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findOrdersWithFilters(null, null, pageable)
                .map(mapper::toDomain);
    }
}
