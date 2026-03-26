package com.example.spring_ecom.service.coupon;

import com.example.spring_ecom.domain.coupon.Coupon;
import com.example.spring_ecom.repository.database.coupon.CouponEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

public interface CouponUseCase {
    
    // ========== QUERY OPERATIONS ==========
    
    Page<Coupon> findAll(Pageable pageable);
    
    Page<Coupon> findActiveCoupons(Pageable pageable);
    
    Optional<Coupon> findById(Long id);
    
    Optional<Coupon> findByCode(String code);
    
    /**
     * Validate a coupon code and return the discount amount for a given order total
     */
    Optional<CouponValidationResult> validateCoupon(String code, BigDecimal orderTotal);
    
    // ========== COMMAND OPERATIONS ==========
    
    Optional<Coupon> create(Coupon coupon);
    
    Optional<Coupon> update(Long id, Coupon coupon);
    
    void delete(Long id);
    
    /**
     * Increment usage count when coupon is applied to an order
     */
    void incrementUsage(Long couponId);
    
    // ========== INNER RECORD ==========
    
    record CouponValidationResult(
        boolean valid,
        String message,
        Coupon coupon,
        BigDecimal discountAmount
    ) {
        public static CouponValidationResult valid(Coupon coupon, BigDecimal discountAmount) {
            return new CouponValidationResult(true, "Coupon is valid", coupon, discountAmount);
        }
        
        public static CouponValidationResult invalid(String message) {
            return new CouponValidationResult(false, message, null, BigDecimal.ZERO);
        }
    }
}
