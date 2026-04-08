package com.example.spring_ecom.controller.api.admin.coupon;

import com.example.spring_ecom.controller.api.coupon.model.*;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.coupon.Coupon;
import com.example.spring_ecom.repository.database.coupon.DiscountType;
import com.example.spring_ecom.service.coupon.CouponUseCase;
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
import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/v1/api/admin/coupons")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Coupons", description = "Admin APIs for coupon management")
public class AdminCouponController {
    
    private final CouponUseCase couponUseCase;
    private final CouponResponseMapper responseMapper;
    
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
        Coupon created = couponUseCase.createWithNotification(
                coupon, 
                request.notificationType(), 
                request.targetUserIds())
                .orElseThrow(() -> new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to create coupon"));
        
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
            null,
            request.code(),
            request.description(),
            request.discountType(),
            request.discountValue(),
            Objects.nonNull(request.minOrderValue()) ? request.minOrderValue() : BigDecimal.ZERO,
            request.maxDiscount(),
            request.usageLimit(),
            0,
            request.startDate(),
            request.endDate(),
            Objects.nonNull(request.isActive()) ? request.isActive() : true,
            null, null, null
        );
    }
}
