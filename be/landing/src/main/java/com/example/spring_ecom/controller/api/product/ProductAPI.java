package com.example.spring_ecom.controller.api.product;

import com.example.spring_ecom.controller.api.product.model.ProductRequest;
import com.example.spring_ecom.controller.api.product.model.ProductResponse;
import com.example.spring_ecom.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product", description = "Product management APIs")
@RequestMapping("/api/products")
public interface ProductAPI {
    
    @Operation(summary = "Get all products with pagination")
    @GetMapping
    ApiResponse<Page<ProductResponse>> getAllProducts(@Parameter(hidden = true) Pageable pageable);
    
    @Operation(summary = "Get product by id")
    @GetMapping("/{id}")
    ApiResponse<ProductResponse> getProductById(@PathVariable Long id);
    
    @Operation(summary = "Get product by slug")
    @GetMapping("/slug/{slug}")
    ApiResponse<ProductResponse> getProductBySlug(@PathVariable String slug);
    
    @Operation(summary = "Get products by category slug")
    @GetMapping("/category/{slug}")
    ApiResponse<Page<ProductResponse>> getProductsByCategory(
            @PathVariable String slug, 
            @Parameter(hidden = true) Pageable pageable);
    
    @Operation(summary = "Search products by keyword")
    @GetMapping("/search")
    ApiResponse<Page<ProductResponse>> searchProducts(
            @RequestParam String keyword, 
            @Parameter(hidden = true) Pageable pageable);
    
    @Operation(summary = "Get bestseller products")
    @GetMapping("/bestseller")
    ApiResponse<Page<ProductResponse>> getBestsellerProducts(@Parameter(hidden = true) Pageable pageable);
    
    @Operation(summary = "Create new product")
    @PostMapping
    ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request);
    
    @Operation(summary = "Update product")
    @PutMapping("/{id}")
    ApiResponse<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request);
    
    @Operation(summary = "Delete product")
    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteProduct(@PathVariable Long id);
}
