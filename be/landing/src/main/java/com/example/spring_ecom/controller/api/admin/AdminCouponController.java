package com.example.spring_ecom.controller.api.admin;

import com.example.spring_ecom.controller.api.coupon.model.*;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.coupon.Coupon;
import com.example.spring_ecom.emqx.domain.NotificationEvent;
import com.example.spring_ecom.repository.database.coupon.DiscountType;
import com.example.spring_ecom.service.coupon.CouponUseCase;
import com.example.spring_ecom.service.notification.NotificationUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminCouponController implements AdminCouponAPI {
    
    private final CouponUseCase couponUseCase;
    private final CouponResponseMapper responseMapper;
    private final NotificationUseCase notificationUseCase;
    
    @Override
    public ResponseEntity<ApiResponse<Page<CouponResponse>>> getAllCoupons(Pageable pageable) {
        Page<CouponResponse> coupons = couponUseCase.findAll(pageable)
                .map(responseMapper::toResponse);
        return ResponseEntity.ok(ApiResponse.Success.of(coupons));
    }
    
    @Override
    public ResponseEntity<ApiResponse<CouponResponse>> getCouponById(Long id) {
        return couponUseCase.findById(id)
                .map(coupon -> ResponseEntity.ok(ApiResponse.Success.of(responseMapper.toResponse(coupon))))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Override
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(CouponRequest request) {
        log.info("Creating coupon: {}", request.code());
        
        Coupon coupon = toDomain(request);
        Coupon created = couponUseCase.create(coupon)
                .orElseThrow(() -> new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to create coupon"));
        
        // Send notification based on notification type
        sendCouponNotification(created, request.notificationType(), request.targetUserIds());
        
        return ResponseEntity.ok(ApiResponse.Success.of(responseMapper.toResponse(created)));
    }
    
    @Override
    public ResponseEntity<ApiResponse<CouponResponse>> updateCoupon(Long id, CouponRequest request) {
        log.info("Updating coupon: {}", id);
        
        Coupon coupon = toDomain(request);
        Coupon updated = couponUseCase.update(id, coupon)
                .orElseThrow(() -> new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to update coupon"));
        
        return ResponseEntity.ok(ApiResponse.Success.of(responseMapper.toResponse(updated)));
    }
    
    @Override
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(Long id) {
        log.info("Deleting coupon: {}", id);
        couponUseCase.delete(id);
        return ResponseEntity.ok(ApiResponse.Success.of());
    }
    
    // ========== HELPER METHODS ==========
    
    private Coupon toDomain(CouponRequest request) {
        return new Coupon(
            null, // id
            request.code(),
            request.description(),
            request.discountType(),
            request.discountValue(),
            request.minOrderValue() != null ? request.minOrderValue() : BigDecimal.ZERO,
            request.maxDiscount(),
            request.usageLimit(),
            0,
            request.startDate(),
            request.endDate(),
            request.isActive() != null ? request.isActive() : true,
            null, null, null 
        );
    }
    
    /**
     * - NONE: Don't send notification
     * - BROADCAST: Send to all users
     * - TARGETED: Send to specific users
     */
    private void sendCouponNotification(Coupon coupon, CouponRequest.NotificationType notificationType, List<Long> targetUserIds) {
        if (notificationType == null || notificationType == CouponRequest.NotificationType.NONE) {
            log.info("[COUPON] No notification requested for coupon: {}", coupon.code());
            return;
        }
        
        try {
            String title = "🎉 Coupon mới!";
            String message = String.format("Sử dụng mã %s để giảm %s", 
                    coupon.code(), 
                    formatDiscount(coupon));
            String actionUrl = "/coupons/" + coupon.id();
            
            if (notificationType == CouponRequest.NotificationType.BROADCAST) {
                // Broadcast to all users
                NotificationEvent event = createCouponEvent(coupon, title, message, actionUrl, null);
                notificationUseCase.broadcast(event);
                log.info("[COUPON] Broadcast notification sent for coupon: {}", coupon.code());
                
            } else if (notificationType == CouponRequest.NotificationType.TARGETED) {
                // Send to specific users
                if (targetUserIds == null || targetUserIds.isEmpty()) {
                    log.warn("[COUPON] TARGETED notification requested but no target users provided");
                    return;
                }
                
                for (Long userId : targetUserIds) {
                    NotificationEvent event = createCouponEvent(coupon, title, message, actionUrl, userId);
                    notificationUseCase.sendToUser(event);
                }
                log.info("[COUPON] Targeted notification sent to {} users for coupon: {}", 
                        targetUserIds.size(), coupon.code());
            }
        } catch (Exception e) {
            log.error("[COUPON] Failed to send notification: {}", e.getMessage());
            // Don't throw - coupon is already created successfully
        }
    }
    
    private NotificationEvent createCouponEvent(Coupon coupon, String title, String message, 
            String actionUrl, Long userId) {
        return new NotificationEvent(
                UUID.randomUUID().toString(),
                "NEW_COUPON",
                Instant.now(),
                "admin-coupon",
                null, // notificationId
                userId, // null for broadcast, specific userId for targeted
                "NEW_COUPON",
                title,
                message,
                coupon.id(),
                "COUPON",
                null, // imageUrl
                actionUrl,
                false,
                LocalDateTime.now()
        );
    }
    
    private String formatDiscount(Coupon coupon) {
        if (coupon.discountType() == DiscountType.PERCENTAGE) {
            return coupon.discountValue() + "%";
        } else {
            return coupon.discountValue() + "đ";
        }
    }
}
