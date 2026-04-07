package com.example.spring_ecom.controller.api.admin.coupon;

import com.example.spring_ecom.controller.api.coupon.model.*;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.coupon.Coupon;
import com.example.spring_ecom.domain.notification.Notification;
import com.example.spring_ecom.repository.database.coupon.DiscountType;
import com.example.spring_ecom.service.coupon.CouponUseCase;
import com.example.spring_ecom.service.notification.NotificationCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api/admin/coupons")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Coupons", description = "Admin APIs for coupon management")
public class AdminCouponController {
    
    private final CouponUseCase couponUseCase;
    private final CouponResponseMapper responseMapper;
    private final NotificationCommandService notificationCommandService;
    
    @Operation(summary = "Get all coupons", description = "Get all coupons (admin only)")
    @GetMapping
    @PreAuthorize("hasAuthority('COUPON_VIEW') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<CouponResponse>>> getAllCoupons(Pageable pageable) {
        Page<CouponResponse> coupons = couponUseCase.findAll(pageable)
                .map(responseMapper::toResponse);
        return ResponseEntity.ok(ApiResponse.Success.of(coupons));
    }
    
    @Operation(summary = "Get coupon by ID", description = "Get coupon details by ID (admin only)")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COUPON_VIEW') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CouponResponse>> getCouponById(
            @Parameter(description = "Coupon ID") @PathVariable Long id) {
        return couponUseCase.findById(id)
                .map(coupon -> ResponseEntity.ok(ApiResponse.Success.of(responseMapper.toResponse(coupon))))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Create coupon", description = "Create a new coupon (admin only)")
    @PostMapping
    @PreAuthorize("hasAuthority('COUPON_CREATE') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(
            @Valid @RequestBody CouponRequest request) {
        log.info("Creating coupon: {}", request.code());
        
        Coupon coupon = toDomain(request);
        Coupon created = couponUseCase.create(coupon)
                .orElseThrow(() -> new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to create coupon"));
        
        // Send notification based on notification type
        sendCouponNotification(created, request.notificationType(), request.targetUserIds());
        
        return ResponseEntity.ok(ApiResponse.Success.of(responseMapper.toResponse(created)));
    }
    
    @Operation(summary = "Update coupon", description = "Update an existing coupon (admin only)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('COUPON_UPDATE') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CouponResponse>> updateCoupon(
            @Parameter(description = "Coupon ID") @PathVariable Long id,
            @Valid @RequestBody CouponRequest request) {
        log.info("Updating coupon: {}", id);
        
        Coupon coupon = toDomain(request);
        Coupon updated = couponUseCase.update(id, coupon)
                .orElseThrow(() -> new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to update coupon"));
        
        return ResponseEntity.ok(ApiResponse.Success.of(responseMapper.toResponse(updated)));
    }
    
    @Operation(summary = "Delete coupon", description = "Delete a coupon (soft delete, admin only)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('COUPON_DELETE') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(
            @Parameter(description = "Coupon ID") @PathVariable Long id) {
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
            Objects.nonNull(request.minOrderValue()) ? request.minOrderValue() : BigDecimal.ZERO,
            request.maxDiscount(),
            request.usageLimit(),
            0, // usedCount
            request.startDate(),
            request.endDate(),
            Objects.nonNull(request.isActive()) ? request.isActive() : true,
            null, null, null // createdAt, updatedAt, deletedAt
        );
    }
    
    /**
     * Send notification based on notification type
     * - NONE: Don't send notification
     * - BROADCAST: Send to all users via MQTT
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
            // Link to public coupons page
            String actionUrl = "/coupons";
            
            if (notificationType == CouponRequest.NotificationType.BROADCAST) {
                // Broadcast to all users via gRPC -> MQTT
                Notification notification = createCouponNotification(coupon, title, message, actionUrl, null);
                notificationCommandService.broadcast(notification);
                log.info("[COUPON] Broadcast notification sent for coupon: {}", coupon.code());
                
            } else if (notificationType == CouponRequest.NotificationType.TARGETED) {
                // Send to specific users
                if (targetUserIds == null || targetUserIds.isEmpty()) {
                    log.warn("[COUPON] TARGETED notification requested but no target users provided");
                    return;
                }
                
                for (Long userId : targetUserIds) {
                    Notification notification = createCouponNotification(coupon, title, message, actionUrl, userId);
                    notificationCommandService.createAndSend(notification);
                }
                log.info("[COUPON] Targeted notification sent to {} users for coupon: {}", 
                        targetUserIds.size(), coupon.code());
            }
        } catch (Exception e) {
            log.error("[COUPON] Failed to send notification: {}", e.getMessage());
            // Don't throw - coupon is already created successfully
        }
    }
    
    private Notification createCouponNotification(Coupon coupon, String title, String message, 
            String actionUrl, Long userId) {
        return new Notification(
                null, // id
                userId, // null for broadcast, specific userId for targeted
                "NEW_COUPON",
                title,
                message,
                coupon.id(),
                "COUPON",
                null, // imageUrl
                actionUrl,
                false,
                null
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
