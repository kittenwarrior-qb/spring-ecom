package com.example.spring_ecom.service.order.detail;

import com.example.spring_ecom.controller.api.order.model.OrderDetailResponse;
import com.example.spring_ecom.controller.api.order.model.OrderItemResponse;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.order.OrderWithUserDto;
import com.example.spring_ecom.domain.order.OrderItemWithProductDto;
import com.example.spring_ecom.repository.database.order.OrderRepository;
import com.example.spring_ecom.repository.database.order.orderItem.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    
    public OrderDetailResponse getOrderDetail(Long orderId) {
        OrderWithUserDto order = orderRepository.findOrderWithUserById(orderId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Order not found"));
        
        List<OrderItemWithProductDto> orderItems = orderItemRepository.findOrderItemsWithProductByOrderId(orderId);
        
        List<OrderItemResponse> items = orderItems.stream()
                .map(this::toOrderItemResponse)
                .toList();
        
        return new OrderDetailResponse(
                order.id(),
                order.orderNumber(),
                order.userId(),
                order.userEmail(),
                order.status(),
                order.subtotal(),
                order.shippingFee(),
                order.discount(),
                order.total(),
                order.paymentMethod(),
                order.shippingAddress(),
                order.shippingCity(),
                order.shippingDistrict(),
                order.shippingWard(),
                order.recipientName(),
                order.recipientPhone(),
                order.note(),
                items,
                order.createdAt(),
                order.updatedAt(),
                order.cancelledAt()
        );
    }
    
    private OrderItemResponse toOrderItemResponse(OrderItemWithProductDto item) {
        return new OrderItemResponse(
                item.id(),
                item.orderId(),
                item.productId(),
                item.productTitle(),
                item.productCoverImageUrl(),
                item.quantity(),
                item.price(),
                item.subtotal(),
                item.createdAt()
        );
    }
}
