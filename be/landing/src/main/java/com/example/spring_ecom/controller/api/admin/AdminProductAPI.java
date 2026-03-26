package com.example.spring_ecom.controller.api.admin;

import com.example.spring_ecom.controller.api.product.model.ProductRequest;
import com.example.spring_ecom.controller.api.product.model.ProductResponse;
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

@Tag(name = "Admin Product Management", description = "Admin APIs for managing products via gRPC")
@RequestMapping("/api/admin/products")
public interface AdminProductAPI {

    @Operation(summary = "Get all products", description = "Get paginated list of all products")
    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(@Parameter(hidden = true) Pageable pageable);

    @Operation(summary = "Get product by ID", description = "Get product detail by ID")
    @GetMapping("/{productId}")
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long productId);

    @Operation(summary = "Create product", description = "Create new product via gRPC")
    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request);

    @Operation(summary = "Update product", description = "Update existing product via gRPC")
    @PutMapping("/{productId}")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductRequest request);

    @Operation(summary = "Delete product", description = "Delete product via gRPC")
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long productId);
}
