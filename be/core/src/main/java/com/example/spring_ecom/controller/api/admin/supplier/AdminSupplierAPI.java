package com.example.spring_ecom.controller.api.admin.supplier;

import com.example.spring_ecom.controller.api.admin.supplier.model.CreateSupplierRequest;
import com.example.spring_ecom.controller.api.admin.supplier.model.SupplierResponse;
import com.example.spring_ecom.controller.api.admin.supplier.model.UpdateSupplierRequest;
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

@RequestMapping("/v1/api/admin/suppliers")
@Tag(name = "Admin Supplier Management", description = "Admin APIs for managing suppliers")
public interface AdminSupplierAPI {

    @Operation(summary = "Get all suppliers", description = "Get paginated list of suppliers with optional filters")
    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    ResponseEntity<ApiResponse<Page<SupplierResponse>>> getAllSuppliers(
            Pageable pageable,
            @Parameter(description = "Search keyword") @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean isActive);

    @Operation(summary = "Get supplier by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    ResponseEntity<ApiResponse<SupplierResponse>> getSupplierById(
            @Parameter(description = "Supplier ID") @PathVariable Long id);

    @Operation(summary = "Create supplier")
    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(
            @Valid @RequestBody CreateSupplierRequest request);

    @Operation(summary = "Update supplier")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(
            @Parameter(description = "Supplier ID") @PathVariable Long id,
            @Valid @RequestBody UpdateSupplierRequest request);

    @Operation(summary = "Delete supplier", description = "Soft delete supplier")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    ResponseEntity<ApiResponse<Void>> deleteSupplier(
            @Parameter(description = "Supplier ID") @PathVariable Long id);
}

