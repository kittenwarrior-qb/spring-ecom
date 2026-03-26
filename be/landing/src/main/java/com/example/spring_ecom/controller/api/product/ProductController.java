package com.example.spring_ecom.controller.api.product;

import com.example.spring_ecom.controller.api.product.model.ProductResponse;
import com.example.spring_ecom.controller.api.product.model.ProductResponseMapper;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.ratelimit.RateLimit;
import com.example.spring_ecom.core.ratelimit.RateLimitType;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.service.product.ProductUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;

/**
 * User Product Controller
 * CLIENT SERVICE - APIs cho người dùng browse sản phẩm (sách)
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Products", description = "Public APIs for browsing books/products")
public class ProductController {
    
    private final ProductUseCase productUseCase;
    private final ProductResponseMapper responseMapper;
    
    @Operation(summary = "Get all products", description = "Browse all books with pagination")
    @GetMapping
    @RateLimit(type = RateLimitType.IP, limit = 200, duration = 1, unit = ChronoUnit.MINUTES)
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(Pageable pageable) {
        try {
            log.info("User browsing all products");
            // TODO: Call server via gRPC for product data
            Page<ProductResponse> products = productUseCase.findAllWithCategory(pageable)
                    .map(responseMapper::toResponse);
            return ResponseEntity.ok(ApiResponse.Success.of(products));
        } catch (Exception e) {
            log.error("Error getting products: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to get products"));
        }
    }
    
    @Operation(summary = "Get product by ID", description = "Get book detail by ID")
    @GetMapping("/{id}")
    @RateLimit(type = RateLimitType.IP, limit = 300, duration = 1, unit = ChronoUnit.MINUTES)
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        try {
            log.info("User getting product by ID: {}", id);
            // TODO: Call server via gRPC for product data
            ProductResponse product = productUseCase.findById(id)
                    .map(responseMapper::toResponse)
                    .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Product not found"));
            return ResponseEntity.ok(ApiResponse.Success.of(product));
        } catch (BaseException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.Error.of(e.getResponseCode(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting product: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to get product"));
        }
    }
    
    @Operation(summary = "Get product by slug", description = "Get book detail by slug")
    @GetMapping("/slug/{slug}")
    @RateLimit(type = RateLimitType.IP, limit = 300, duration = 1, unit = ChronoUnit.MINUTES)
    public ResponseEntity<ApiResponse<ProductResponse>> getProductBySlug(
            @Parameter(description = "Product slug") @PathVariable String slug) {
        try {
            log.info("User getting product by slug: {}", slug);
            // TODO: Call server via gRPC for product data
            ProductResponse product = productUseCase.findBySlug(slug)
                    .map(responseMapper::toResponse)
                    .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Product not found"));
            return ResponseEntity.ok(ApiResponse.Success.of(product));
        } catch (BaseException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.Error.of(e.getResponseCode(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting product: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to get product"));
        }
    }
    
    @Operation(summary = "Get products by category", description = "Browse books by category")
    @GetMapping("/category/{slug}")
    @RateLimit(type = RateLimitType.IP, limit = 150, duration = 1, unit = ChronoUnit.MINUTES)
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsByCategory(
            @Parameter(description = "Category slug") @PathVariable String slug, 
            Pageable pageable) {
        try {
            log.info("User browsing products by category: {}", slug);
            // TODO: Call server via gRPC for product data
            Page<ProductResponse> products = productUseCase.findByCategorySlug(slug, pageable)
                    .map(responseMapper::toResponse);
            return ResponseEntity.ok(ApiResponse.Success.of(products));
        } catch (Exception e) {
            log.error("Error getting products by category: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to get products"));
        }
    }
    
    @Operation(summary = "Search products", description = "Search books by keyword")
    @GetMapping("/search")
    @RateLimit(type = RateLimitType.IP, limit = 100, duration = 1, unit = ChronoUnit.MINUTES)
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProducts(
            @Parameter(description = "Search keyword") @RequestParam String q, 
            Pageable pageable) {
        try {
            log.info("User searching products with keyword: {}", q);
            // TODO: Call server via gRPC for product search
            Page<ProductResponse> products = productUseCase.searchProducts(q, pageable)
                    .map(responseMapper::toResponse);
            return ResponseEntity.ok(ApiResponse.Success.of(products));
        } catch (Exception e) {
            log.error("Error searching products: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to search products"));
        }
    }
    
    @Operation(summary = "Get bestseller products", description = "Get bestselling books")
    @GetMapping("/bestsellers")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getBestsellerProducts(Pageable pageable) {
        try {
            log.info("User getting bestseller products");
            // TODO: Call server via gRPC for bestseller data
            Page<ProductResponse> products = productUseCase.findBestsellerProducts(pageable)
                    .map(responseMapper::toResponse);
            return ResponseEntity.ok(ApiResponse.Success.of(products));
        } catch (Exception e) {
            log.error("Error getting bestseller products: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to get bestsellers"));
        }
    }
    
    @Operation(summary = "Get featured products", description = "Get featured books")
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getFeaturedProducts(Pageable pageable) {
        try {
            log.info("User getting featured products");
            // TODO: Call server via gRPC for featured data
            Page<ProductResponse> products = productUseCase.findFeaturedProducts(pageable)
                    .map(responseMapper::toResponse);
            return ResponseEntity.ok(ApiResponse.Success.of(products));
        } catch (Exception e) {
            log.error("Error getting featured products: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to get featured products"));
        }
    }
}
