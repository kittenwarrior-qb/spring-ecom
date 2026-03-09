package com.example.spring_ecom.service.order.detail;

import com.example.spring_ecom.controller.api.order.model.OrderDetailResponse;
import com.example.spring_ecom.controller.api.order.model.OrderItemResponse;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.repository.database.order.OrderEntity;
import com.example.spring_ecom.repository.database.order.orderItem.OrderItemEntity;
import com.example.spring_ecom.repository.database.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService {
    
    private final OrderRepository orderRepository;
    
    public OrderDetailResponse getOrderDetail(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Order not found"));
        
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::toOrderItemResponse)
                .toList();
        
        return new OrderDetailResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getUser().getId(),
                order.getStatus(),
                order.getSubtotal(),
                order.getShippingFee(),
                order.getDiscount(),
                order.getTotal(),
                order.getPaymentMethod(),
                order.getShippingAddress(),
                order.getShippingCity(),
                order.getShippingDistrict(),
                order.getShippingWard(),
                order.getRecipientName(),
                order.getRecipientPhone(),
                order.getNote(),
                items,
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getCancelledAt()
        );
    }
    
    private OrderItemResponse toOrderItemResponse(OrderItemEntity item) {
        return new OrderItemResponse(
                item.getId(),
                item.getOrder().getId(),
                item.getProduct().getId(),
                item.getProductTitle(),
                item.getQuantity(),
                item.getPrice(),
                item.getSubtotal(),
                item.getCreatedAt()
        );
    }
}
