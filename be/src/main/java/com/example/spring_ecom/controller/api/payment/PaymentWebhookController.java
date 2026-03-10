package com.example.spring_ecom.controller.api.payment;

import com.example.spring_ecom.controller.api.payment.model.SepayWebhookRequest;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.service.payment.SepayWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("v1/api/payments")
@RequiredArgsConstructor
public class PaymentWebhookController {
    
    private final SepayWebhookService sepayWebhookService;
    
    @PostMapping("/sepay/webhook")
    public ApiResponse<Void> handleSepayWebhook(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody SepayWebhookRequest request
    ) {
        log.info("Received SePay webhook for transaction ID: {}, code: {}", request.id(), request.code());
        
        sepayWebhookService.processPayment(request, authorization);
        
        return ApiResponse.Success.of(null);
    }
}
