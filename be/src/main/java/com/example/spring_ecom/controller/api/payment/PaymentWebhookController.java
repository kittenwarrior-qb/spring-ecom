package com.example.spring_ecom.controller.api.payment;

import com.example.spring_ecom.controller.api.payment.model.PaymentInfoResponse;
import com.example.spring_ecom.controller.api.payment.model.SepayWebhookRequest;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.service.order.payment.SepayWebhookService;
import com.example.spring_ecom.service.payment.PaymentQRService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("v1/api/payments")
@RequiredArgsConstructor
public class PaymentWebhookController {
    
    private final SepayWebhookService sepayWebhookService;
    private final PaymentQRService paymentQRService;
    
    @PostMapping("/sepay/webhook")
    public ApiResponse<Void> handleSepayWebhook(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody SepayWebhookRequest request
    ) {
        log.info("Received SePay webhook for transaction ID: {}, code: {}", request.id(), request.code());
        
        sepayWebhookService.processPayment(request, authorization);
        
        return ApiResponse.Success.of(null);
    }
    
    @PostMapping("/orders/{orderNumber}/create-payment")
    public ApiResponse<PaymentInfoResponse> createPaymentInfo(@PathVariable String orderNumber) {
        log.info("Creating payment info for order: {}", orderNumber);
        
        PaymentInfoResponse paymentInfo = paymentQRService.createPaymentInfo(orderNumber);
        
        return ApiResponse.Success.of(paymentInfo);
    }
    
    @GetMapping("/orders/{orderNumber}/payment-status")
    public ApiResponse<String> getPaymentStatus(@PathVariable String orderNumber) {
        log.info("Checking payment status for order: {}", orderNumber);
        
        // TODO: Implement actual status check
        return ApiResponse.Success.of("PENDING");
    }
}
