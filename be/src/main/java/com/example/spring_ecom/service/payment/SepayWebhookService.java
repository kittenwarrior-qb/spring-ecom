package com.example.spring_ecom.service.payment;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SepayWebhookService {
    
    private final SepayTransactionRepository sepayTransactionRepository;
    private final OrderUseCase orderUseCase;
    
    @Value("${payment.webhook.api-key:}")
    private String webhookApiKey;
    
    @Transactional
    public void processPayment(SepayWebhookRequest request, String authorization) {
        // Verify API key
        if (webhookApiKey != null && !webhookApiKey.isEmpty()) {
            if (authorization == null || !authorization.startsWith("Apikey ")) {
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
        
        // Parse transaction date
        LocalDateTime transactionDate;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            transactionDate = LocalDateTime.parse(request.transactionDate(), formatter);
        } catch (Exception e) {
            log.error("Failed to parse transaction date: {}", request.transactionDate(), e);
            transactionDate = LocalDateTime.now();
        }
        
        // Calculate amounts
        BigDecimal amountIn = "in".equals(request.transferType()) ? 
                BigDecimal.valueOf(request.transferAmount()) : BigDecimal.ZERO;
        BigDecimal amountOut = "out".equals(request.transferType()) ? 
                BigDecimal.valueOf(request.transferAmount()) : BigDecimal.ZERO;
        
        // Create and save transaction
        SepayTransaction transaction = new SepayTransaction(
                null,
                request.id(),
                request.gateway(),
                transactionDate,
                request.accountNumber(),
                request.subAccount(),
                amountIn,
                amountOut,
                BigDecimal.valueOf(request.accumulated()),
                request.code(),
                request.content(),
                request.referenceCode(),
                request.description(),
                request.transferType(),
                BigDecimal.valueOf(request.transferAmount()),
                false,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        
        SepayTransaction savedTransaction = sepayTransactionRepository.save(transaction);
        log.info("SePay transaction {} saved successfully", request.id());
        
        // Process payment if it's an incoming transfer with payment code
        if ("in".equals(request.transferType()) && request.code() != null && !request.code().trim().isEmpty()) {
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
                    transaction.gateway(),
                    transaction.transactionDate(),
                    transaction.accountNumber(),
                    transaction.subAccount(),
                    transaction.amountIn(),
                    transaction.amountOut(),
                    transaction.accumulated(),
                    transaction.code(),
                    transaction.transactionContent(),
                    transaction.referenceCode(),
                    transaction.description(),
                    transaction.transferType(),
                    transaction.transferAmount(),
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