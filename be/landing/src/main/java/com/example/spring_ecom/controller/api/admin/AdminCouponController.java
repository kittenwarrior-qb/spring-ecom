package com.example.spring_ecom.controller.api.admin;

import com.example.spring_ecom.controller.api.coupon.model.*;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.coupon.Coupon;
import com.example.spring_ecom.service.coupon.CouponUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminCouponController implements AdminCouponAPI {
    
    private final CouponUseCase couponUseCase;
    private final CouponResponseMapper responseMapper;
    
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
        return ResponseEntity.ok(ApiResponse.Success.of(null));
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
            0, // usedCount
            request.startDate(),
            request.endDate(),
            request.isActive() != null ? request.isActive() : true,
            null, null, null // createdAt, updatedAt, deletedAt
        );
    }
}
