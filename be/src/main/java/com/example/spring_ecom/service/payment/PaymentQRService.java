package com.example.spring_ecom.service.payment;

import com.example.spring_ecom.controller.api.payment.model.PaymentInfoResponse;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.PaymentMethod;
import com.example.spring_ecom.domain.order.PaymentStatus;
import com.example.spring_ecom.service.order.OrderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentQRService {
    
    private final OrderUseCase orderUseCase;
    
    @Value("${sepay.bank.name:Vietcombank}")
    private String bankName;
    
    @Value("${sepay.bank.code:970436}")
    private String bankCode;
    
    @Value("${sepay.account.number:0123456789}")
    private String accountNumber;
    
    @Value("${sepay.account.name:CONG TY ABC}")
    private String accountName;
    
    @Value("${sepay.payment.timeout:900}") // 15 minutes
    private Long paymentTimeout;
    
    public PaymentInfoResponse createPaymentInfo(String orderNumber) {
        // Find order
        Order order = orderUseCase.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Order not found"));
        
        // Validate order
        if (order.paymentMethod() != PaymentMethod.BANK_TRANSFER) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Order payment method is not BANK_TRANSFER");
        }
        
        if (order.paymentStatus() == PaymentStatus.PAID) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Order already paid");
        }
        
        // Generate QR code content
        String transferContent = order.orderNumber();
        String qrContent = generateQRContent(accountNumber, order.total().longValue(), transferContent);
        
        // Generate QR code URL (using external service)
        String qrCodeUrl = generateQRCodeUrl(qrContent);
        String qrCodeBase64 = generateQRCodeBase64(qrContent);
        
        log.info("Generated payment info for order: {}, amount: {}", orderNumber, order.total());
        
        return new PaymentInfoResponse(
                order.orderNumber(),
                order.total(),
                bankName,
                accountNumber,
                accountName,
                transferContent,
                qrCodeUrl,
                qrCodeBase64,
                paymentTimeout,
                "PENDING"
        );
    }
    
    private String generateQRContent(String accountNumber, Long amount, String content) {
        // VietQR format: https://vietqr.io/
        return String.format("https://img.vietqr.io/image/%s-%s-%d.png?addInfo=%s&accountName=%s",
                bankCode, // Dùng bank code từ config
                accountNumber,
                amount,
                content,
                accountName.replace(" ", "%20")
        );
    }
    
    private String generateQRCodeUrl(String qrContent) {
        // Using VietQR service
        return qrContent;
    }
    
    private String generateQRCodeBase64(String qrContent) {
        // For demo, return empty base64
        // In production, generate actual QR code image and convert to base64
        try {
            String demoQR = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==";
            return demoQR;
        } catch (Exception e) {
            log.error("Failed to generate QR code base64", e);
            return "";
        }
    }
}