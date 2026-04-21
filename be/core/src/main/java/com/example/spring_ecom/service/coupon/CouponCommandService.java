package com.example.spring_ecom.service.coupon;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.coupon.Coupon;
import com.example.spring_ecom.domain.notification.Notification;
import com.example.spring_ecom.repository.database.coupon.CouponEntity;
import com.example.spring_ecom.repository.database.coupon.CouponEntityMapper;
import com.example.spring_ecom.repository.database.coupon.CouponRepository;
import com.example.spring_ecom.repository.database.coupon.DiscountType;
import com.example.spring_ecom.service.notification.NotificationCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponCommandService {
    
    private final CouponRepository couponRepository;
    private final CouponEntityMapper mapper;
    private final NotificationCommandService notificationCommandService;
    
    @Transactional
    public Optional<Coupon> create(Coupon coupon) {
        validateCoupon(coupon);
        
        // Check if code already exists
        if (couponRepository.existsByCodeAndDeletedAtIsNull(coupon.code())) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Coupon code already exists: " + coupon.code());
        }
        
        CouponEntity entity = mapper.toEntity(coupon);
        CouponEntity saved = couponRepository.save(entity);
        
        return Optional.of(mapper.toDomain(saved));
    }
    
    @Transactional
    public Optional<Coupon> createWithNotification(Coupon coupon, CouponUseCase.NotificationType notificationType, java.util.List<Long> targetUserIds) {
        Coupon created = create(coupon).orElseThrow(
                () -> new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to create coupon"));
        
        sendCouponNotification(created, notificationType, targetUserIds);
        
        return Optional.of(created);
    }
    
    @Transactional
    public Optional<Coupon> update(Long id, Coupon coupon) {
        CouponEntity entity = findActiveCouponById(id);
        
        validateCoupon(coupon);
        
        // Check code uniqueness if changed
        if (!entity.getCode().equals(coupon.code())) {
            if (couponRepository.existsByCodeAndDeletedAtIsNull(coupon.code())) {
                throw new BaseException(ResponseCode.BAD_REQUEST, "Coupon code already exists: " + coupon.code());
            }
        }
        
        mapper.update(entity, coupon);
        
        CouponEntity saved = couponRepository.save(entity);
        return Optional.of(mapper.toDomain(saved));
    }
    
    @Transactional
    public void delete(Long id) {
        CouponEntity entity = findActiveCouponById(id);
        mapper.markAsDeleted(entity, null);
        couponRepository.save(entity);
    }
    
    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2)
    )
    public void incrementUsage(Long couponId) {
        CouponEntity entity = couponRepository.findByIdAndDeletedAtIsNull(couponId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Coupon not found"));
        
        // Check usage limit
        if (Objects.nonNull(entity.getUsageLimit()) && entity.getUsedCount() >= entity.getUsageLimit()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Coupon usage limit reached");
        }
        
        entity.setUsedCount(entity.getUsedCount() + 1);
        couponRepository.save(entity);
        
        log.info("[COUPON] Usage incremented: couponId={}, newCount={}", couponId, entity.getUsedCount());
    }

    @Recover
    public void recoverIncrementUsage(OptimisticLockingFailureException ex, Long couponId) {
        throw new BaseException(ResponseCode.CONFLICT,
                "Coupon is being used concurrently, please retry. couponId=" + couponId);
    }

    // ========== HELPER METHODS ==========
    
    private CouponEntity findActiveCouponById(Long id) {
        return couponRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Coupon not found"));
    }
    
    private void validateCoupon(Coupon coupon) {
        // Validate discount value
        if (Objects.isNull(coupon.discountValue()) || coupon.discountValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Discount value must be positive");
        }
        
        // Validate percentage discount <= 100
        if (coupon.discountType().name().equals("PERCENTAGE") && 
            coupon.discountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Percentage discount cannot exceed 100%");
        }
        
        // Validate dates
        if (Objects.nonNull(coupon.startDate()) && Objects.nonNull(coupon.endDate())) {
            if (!coupon.endDate().isAfter(coupon.startDate())) {
                throw new BaseException(ResponseCode.BAD_REQUEST, "End date must be after start date");
            }
        }
        
        // Validate max discount
        if (Objects.nonNull(coupon.maxDiscount()) && coupon.maxDiscount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Max discount must be positive");
        }
        
        // Validate min order value
        if (Objects.nonNull(coupon.minOrderValue()) && coupon.minOrderValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Minimum order value cannot be negative");
        }
        
        // Validate usage limit
        if (Objects.nonNull(coupon.usageLimit()) && coupon.usageLimit() <= 0) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Usage limit must be positive");
        }
    }
    
    // ========== NOTIFICATION METHODS ==========
    
    private void sendCouponNotification(Coupon coupon, CouponUseCase.NotificationType notificationType, java.util.List<Long> targetUserIds) {
        if (notificationType == null || notificationType == CouponUseCase.NotificationType.NONE) {
            log.info("[COUPON] No notification requested for coupon: {}", coupon.code());
            return;
        }
        
        try {
            String title = "🎁 Coupon mới!";
            String message = String.format("Sử dụng mã %s để giảm %s",
                    coupon.code(),
                    formatDiscount(coupon));
            String actionUrl = "/coupons";
            
            if (notificationType == CouponUseCase.NotificationType.BROADCAST) {
                Notification notification = createCouponNotification(coupon, title, message, actionUrl, null);
                notificationCommandService.broadcast(notification);
                log.info("[COUPON] Broadcast notification sent for coupon: {}", coupon.code());
                
            } else if (notificationType == CouponUseCase.NotificationType.TARGETED) {
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
        }
    }
    
    private Notification createCouponNotification(Coupon coupon, String title, String message,
            String actionUrl, Long userId) {
        return new Notification(
                null,
                userId,
                "NEW_COUPON",
                title,
                message,
                coupon.id(),
                "COUPON",
                null,
                actionUrl,
                false,
                null
        );
    }
    
    private String formatDiscount(Coupon coupon) {
        if (coupon.discountType() == DiscountType.PERCENTAGE) {
            return coupon.discountValue() + "%";
        } else {
            return coupon.discountValue() + "d";
        }
    }
}
