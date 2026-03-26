package com.example.spring_ecom.controller.api.admin.product;

import com.example.spring_ecom.controller.api.product.model.CreateProductRequest;
import com.example.spring_ecom.controller.api.product.model.ProductResponse;
import com.example.spring_ecom.controller.api.product.model.UpdateProductRequest;
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

@RequestMapping("/v1/api/admin/products")
@Tag(name = "Admin Product Management", description = "Admin APIs for managing books/products")
public interface AdminProductAPI {

    @Operation(summary = "Get all products", description = "Get paginated list of all products")
    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(
            Pageable pageable,
            @Parameter(description = "Search term") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by category") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean isActive);

    @Operation(summary = "Get product by ID", description = "Get product detail by ID")
    @GetMapping("/{productId}")
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @Parameter(description = "Product ID") @PathVariable Long productId);

    @Operation(summary = "Create product", description = "Create new book/product")
    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request);

    @Operation(summary = "Update product", description = "Update existing product")
    @PutMapping("/{productId}")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @Parameter(description = "Product ID") @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request);

    @Operation(summary = "Delete product", description = "Soft delete product")
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    ResponseEntity<ApiResponse<Void>> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable Long productId);

    @Operation(summary = "Update product stock", description = "Update product stock quantity")
    @PutMapping("/{productId}/stock")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    ResponseEntity<ApiResponse<ProductResponse>> updateProductStock(
            @Parameter(description = "Product ID") @PathVariable Long productId,
            @RequestParam int stockQuantity);
}
