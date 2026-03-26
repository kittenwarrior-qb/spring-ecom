package com.example.spring_ecom.controller.api.coupon;

import com.example.spring_ecom.controller.api.coupon.model.*;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.domain.coupon.Coupon;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Coupons", description = "Public APIs for coupon validation and browsing")
public class CouponController {
    
    private final CouponUseCase couponUseCase;
    private final CouponResponseMapper responseMapper;
    
    @Operation(summary = "Get active coupons", description = "Browse all currently active coupons")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CouponResponse>>> getActiveCoupons(Pageable pageable) {
        Page<CouponResponse> coupons = couponUseCase.findActiveCoupons(pageable)
                .map(responseMapper::toResponse);
        return ResponseEntity.ok(ApiResponse.Success.of(coupons));
    }
    
    @Operation(summary = "Get coupon by code", description = "Get coupon details by code")
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<CouponResponse>> getCouponByCode(
            @Parameter(description = "Coupon code") @PathVariable String code) {
        return couponUseCase.findByCode(code)
                .map(coupon -> ResponseEntity.ok(ApiResponse.Success.of(responseMapper.toResponse(coupon))))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Validate coupon", description = "Validate a coupon code and get discount amount for an order total")
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<CouponValidationResponse>> validateCoupon(
            @Valid @RequestBody CouponValidationRequest request) {
        log.info("Validating coupon: {} for order total: {}", request.code(), request.orderTotal());
        
        var validationResult = couponUseCase.validateCoupon(request.code(), request.orderTotal());
        
        if (validationResult.isPresent()) {
            var result = validationResult.get();
            CouponResponse couponResponse = responseMapper.toResponse(result.coupon());
            return ResponseEntity.ok(ApiResponse.Success.of(
                    CouponValidationResponse.valid(couponResponse, result.discountAmount())));
        } else {
            return ResponseEntity.ok(ApiResponse.Success.of(
                    CouponValidationResponse.invalid("Invalid or expired coupon")));
        }
    }
}
