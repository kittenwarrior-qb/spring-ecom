package com.example.spring_ecom.controller.api.admin;

import com.example.spring_ecom.controller.api.coupon.model.CouponRequest;
import com.example.spring_ecom.controller.api.coupon.model.CouponResponse;
import com.example.spring_ecom.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin Coupons", description = "Admin APIs for coupon management")
@RequestMapping("/api/admin/coupons")
public interface AdminCouponAPI {

    @Operation(summary = "Get all coupons", description = "Get all coupons (admin only)")
    @GetMapping
    @PreAuthorize("hasAuthority('COUPON_VIEW') or hasRole('ADMIN')")
    ResponseEntity<ApiResponse<Page<CouponResponse>>> getAllCoupons(@Parameter(hidden = true) Pageable pageable);

    @Operation(summary = "Get coupon by ID", description = "Get coupon details by ID (admin only)")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COUPON_VIEW') or hasRole('ADMIN')")
    ResponseEntity<ApiResponse<CouponResponse>> getCouponById(@PathVariable Long id);

    @Operation(summary = "Create coupon", description = "Create a new coupon (admin only)")
    @PostMapping
    @PreAuthorize("hasAuthority('COUPON_CREATE') or hasRole('ADMIN')")
    ResponseEntity<ApiResponse<CouponResponse>> createCoupon(@Valid @RequestBody CouponRequest request);

    @Operation(summary = "Update coupon", description = "Update an existing coupon (admin only)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('COUPON_UPDATE') or hasRole('ADMIN')")
    ResponseEntity<ApiResponse<CouponResponse>> updateCoupon(
            @PathVariable Long id,
            @Valid @RequestBody CouponRequest request);

    @Operation(summary = "Delete coupon", description = "Delete a coupon (soft delete, admin only)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('COUPON_DELETE') or hasRole('ADMIN')")
    ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable Long id);
}
