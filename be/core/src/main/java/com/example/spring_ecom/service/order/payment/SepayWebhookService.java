package com.example.spring_ecom.service.order.payment;

import com.example.spring_ecom.controller.api.payment.model.SepayWebhookRequest;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.PaymentMethod;
import com.example.spring_ecom.domain.order.PaymentStatus;
import com.example.spring_ecom.domain.order.SepayTransaction;
import com.example.spring_ecom.repository.database.SepayTransactionRepository;
import com.example.spring_ecom.service.order.OrderUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SepayWebhookService {
    
    private final SepayTransactionRepository sepayTransactionRepository;
    private final OrderUseCase orderUseCase;
    private final ObjectMapper objectMapper;
    
    @Value("${payment.webhook.api-key:}")
    private String webhookApiKey;
    
    @Transactional
    public void processPayment(SepayWebhookRequest request, String authorization) {
        // Verify API key
        if (Objects.nonNull(webhookApiKey) && !webhookApiKey.isEmpty()) {
            if (Objects.isNull(authorization) || !authorization.startsWith("Apikey ")) {
                log.error("Invalid webhook API key format");
                throw new BaseException(ResponseCode.UNAUTHORIZED, "Invalid API key format");
            }
            
            String providedKey = authorization.substring(7); // Remove 'Apikey ' prefix
            if (!providedKey.equals(webhookApiKey)) {
                log.error("Invalid webhook API key");
                throw new BaseException(ResponseCode.UNAUTHORIZED, "Invalid API key");
            }
        }
        
        // Check for duplicate transaction
        Optional<SepayTransaction> existingTransaction = sepayTransactionRepository.findBySepayId(request.id());
        if (existingTransaction.isPresent()) {
            log.info("Transaction {} already exists, skipping...", request.id());
            return;
        }
        
        // Convert request to JsonNode for storage
        JsonNode webhookData;
        try {
            webhookData = objectMapper.valueToTree(request);
        } catch (Exception e) {
            log.error("Failed to convert webhook request to JSON", e);
            throw new BaseException(ResponseCode.BAD_REQUEST, "Invalid webhook data format");
        }
        
        // Create and save transaction
        SepayTransaction transaction = new SepayTransaction(
                null,
                request.id(),
                webhookData,
                request.code(),
                BigDecimal.valueOf(request.transferAmount()),
                request.transferType(),
                false,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        
        SepayTransaction savedTransaction = sepayTransactionRepository.save(transaction);
        log.info("SePay transaction {} saved successfully", request.id());
        
        // Process payment if it's an incoming transfer with payment code
        if ("in".equals(request.transferType()) && Objects.nonNull(request.code()) && !request.code().trim().isEmpty()) {
            processOrderPayment(savedTransaction);
        }
    }
    
    private void processOrderPayment(SepayTransaction transaction) {
        try {
            // Find order by payment code (order number)
            Optional<Order> orderOpt = orderUseCase.findByOrderNumber(transaction.code());
            
            if (orderOpt.isEmpty()) {
                log.warn("Order not found for payment code: {}", transaction.code());
                return;
            }
            
            Order order = orderOpt.get();
            
            // Check if order is already paid
            if (order.paymentStatus() == PaymentStatus.PAID) {
                log.info("Order {} already paid", order.orderNumber());
                return;
            }
            
            // Check if order uses BANK_TRANSFER payment method (SePay)
            if (order.paymentMethod() != PaymentMethod.BANK_TRANSFER) {
                log.warn("Order {} payment method is {}, expected BANK_TRANSFER for SePay", 
                        order.orderNumber(), order.paymentMethod());
                // Still process but log the mismatch
            }
            
            // Verify payment amount matches order total
            if (transaction.transferAmount().compareTo(order.total()) != 0) {
                log.warn("Payment amount mismatch for order {}: expected {}, received {}", 
                        order.orderNumber(), order.total(), transaction.transferAmount());
                // Still process but log the discrepancy
            }
            
            // Update order payment status
            orderUseCase.updatePaymentStatus(order.id(), PaymentStatus.PAID);
            
            // If order is still pending, move to confirmed
            if (order.status() == OrderStatus.PENDING) {
                orderUseCase.updateOrderStatus(order.id(), OrderStatus.CONFIRMED);
            }
            
            // Update transaction to mark as processed and link to order
            SepayTransaction updatedTransaction = new SepayTransaction(
                    transaction.id(),
                    transaction.sepayId(),
                    transaction.webhookData(),
                    transaction.code(),
                    transaction.transferAmount(),
                    transaction.transferType(),
                    true,
                    order.id(),
                    transaction.createdAt(),
                    LocalDateTime.now()
            );
            
            sepayTransactionRepository.save(updatedTransaction);
            
            log.info("Payment processed successfully for order: {}, amount: {}", 
                    order.orderNumber(), transaction.transferAmount());
                    
        } catch (Exception e) {
            log.error("Failed to process order payment for transaction {}: {}", 
                    transaction.sepayId(), e.getMessage(), e);
        }
    }
}