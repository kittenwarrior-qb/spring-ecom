package com.example.spring_ecom.controller.api.payment;

import com.example.spring_ecom.controller.api.payment.model.PayOSWebhookRequest;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.service.payment.PaymentWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentWebhookController {
    
    private final PaymentWebhookService paymentWebhookService;
    
    @PostMapping("/webhook")
    public ApiResponse<Void> handlePaymentWebhook(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody PayOSWebhookRequest request
    ) {
        log.info("Received payment webhook for orderCode: {}", request.orderCode());
        
        paymentWebhookService.processPayment(request, authorization);
        
        return ApiResponse.Success.of(null);
    }
}
