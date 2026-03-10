package com.example.spring_ecom.service.payment;

import com.example.spring_ecom.controller.api.payment.model.PayOSWebhookRequest;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.service.order.OrderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentWebhookService {
    
    private final OrderUseCase orderUseCase;
    
    @Value("${payment.webhook.api-key:}")
    private String webhookApiKey;
    
    @Transactional
    public void processPayment(PayOSWebhookRequest request, String authorization) {
        // Verify API key
        if (webhookApiKey != null && !webhookApiKey.isEmpty()) {
            if (authorization == null || !authorization.equals("Bearer " + webhookApiKey)) {
                log.error("Invalid webhook API key");
                throw new BaseException(ResponseCode.UNAUTHORIZED, "Invalid API key");
            }
        }
        
        // Verify payment success
        if (!"00".equals(request.code())) {
            log.warn("Payment not successful: code={}, desc={}", request.code(), request.desc());
            return;
        }
        
        // Find order by orderCode
        Order order = orderUseCase.findByOrderNumber(String.valueOf(request.orderCode()))
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Order not found"));
        
        // Check if already paid
        if (order.status() == OrderStatus.PAID || order.status() == OrderStatus.CONFIRMED) {
            log.info("Order {} already paid", request.orderCode());
            return;
        }
        
        // Update order status to PAID
        orderUseCase.updateOrderStatus(order.id(), OrderStatus.PAID);
        
        log.info("Payment processed successfully for order: {}, amount: {}", 
                request.orderCode(), request.amount());
    }
}
