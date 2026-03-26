package com.example.spring_ecom.service.coupon;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.coupon.Coupon;
import com.example.spring_ecom.repository.database.coupon.CouponEntity;
import com.example.spring_ecom.repository.database.coupon.CouponEntityMapper;
import com.example.spring_ecom.repository.database.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponQueryService {
    
    private final CouponRepository couponRepository;
    private final CouponEntityMapper mapper;
    
    public Page<Coupon> findAll(Pageable pageable) {
        return couponRepository.findByDeletedAtIsNull(pageable)
                .map(mapper::toDomain);
    }
    
    public Page<Coupon> findActiveCoupons(Pageable pageable) {
        return couponRepository.findActiveCoupons(LocalDateTime.now(), pageable)
                .map(mapper::toDomain);
    }
    
    public Optional<Coupon> findById(Long id) {
        return couponRepository.findByIdAndDeletedAtIsNull(id)
                .map(mapper::toDomain);
    }
    
    public Optional<Coupon> findByCode(String code) {
        return couponRepository.findByCodeAndDeletedAtIsNull(code)
                .map(mapper::toDomain);
    }
    
    public Optional<CouponEntity> findEntityById(Long id) {
        return couponRepository.findByIdAndDeletedAtIsNull(id);
    }
    
    public Optional<CouponEntity> findValidCouponEntityByCode(String code) {
        return couponRepository.findValidCouponByCode(code, LocalDateTime.now());
    }
    
    /**
     * Validate a coupon for use with a given order total
     */
    public CouponUseCase.CouponValidationResult validateCoupon(String code, BigDecimal orderTotal) {
        // Find coupon by code
        Optional<CouponEntity> entityOpt = couponRepository.findByCodeAndDeletedAtIsNull(code);
        if (entityOpt.isEmpty()) {
            return CouponUseCase.CouponValidationResult.invalid("Coupon not found");
        }
        
        CouponEntity entity = entityOpt.get();
        Coupon coupon = mapper.toDomain(entity);
        
        // Check if active
        if (!entity.getIsActive()) {
            return CouponUseCase.CouponValidationResult.invalid("Coupon is not active");
        }
        
        // Check validity period
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(entity.getStartDate())) {
            return CouponUseCase.CouponValidationResult.invalid("Coupon is not yet valid");
        }
        if (now.isAfter(entity.getEndDate())) {
            return CouponUseCase.CouponValidationResult.invalid("Coupon has expired");
        }
        
        // Check usage limit
        if (entity.getUsageLimit() != null && entity.getUsedCount() >= entity.getUsageLimit()) {
            return CouponUseCase.CouponValidationResult.invalid("Coupon usage limit reached");
        }
        
        // Check minimum order value
        if (orderTotal.compareTo(entity.getMinOrderValue()) < 0) {
            return CouponUseCase.CouponValidationResult.invalid(
                    "Minimum order value of " + entity.getMinOrderValue() + " required");
        }
        
        // Calculate discount
        BigDecimal discountAmount = coupon.calculateDiscount(orderTotal);
        
        return CouponUseCase.CouponValidationResult.valid(coupon, discountAmount);
    }
}
