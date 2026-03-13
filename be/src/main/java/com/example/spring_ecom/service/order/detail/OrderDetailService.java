package com.example.spring_ecom.service.order.detail;

import com.example.spring_ecom.controller.api.order.orderItem.model.OrderDetailResponse;
import com.example.spring_ecom.service.order.OrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderDetailService {
    
    private final OrderQueryService orderQueryService;
    
    public Optional<OrderDetailResponse> getOrderDetail(Long orderId) {
        return orderQueryService.getOrderDetail(orderId);
    }
}