package com.example.spring_ecom.service.coupon;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.coupon.Coupon;
import com.example.spring_ecom.repository.database.coupon.CouponEntity;
import com.example.spring_ecom.repository.database.coupon.CouponEntityMapper;
import com.example.spring_ecom.repository.database.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public void incrementUsage(Long couponId) {
        CouponEntity entity = couponRepository.findByIdAndDeletedAtIsNull(couponId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Coupon not found"));
        
        entity.setUsedCount(entity.getUsedCount() + 1);
        couponRepository.save(entity);
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
}
