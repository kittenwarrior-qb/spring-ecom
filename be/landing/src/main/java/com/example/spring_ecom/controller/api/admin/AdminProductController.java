package com.example.spring_ecom.controller.api.admin;

import com.example.spring_ecom.controller.api.product.model.ProductRequest;
import com.example.spring_ecom.controller.api.product.model.ProductResponse;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.service.product.admin.AdminProductUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Product Controller - CLIENT SERVICE
 * Calls Core service via gRPC for product CRUD operations
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminProductController implements AdminProductAPI {

    private final AdminProductUseCase adminProductUseCase;

    @Override
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(Pageable pageable) {
        Page<ProductResponse> products = adminProductUseCase.getAllProducts(pageable);
        return ResponseEntity.ok(ApiResponse.Success.of(products));
    }

    @Override
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(Long productId) {
        return adminProductUseCase.getProductById(productId)
                .map(product -> ResponseEntity.ok(ApiResponse.Success.of(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(ProductRequest request) {
        return adminProductUseCase.createProduct(request)
                .map(created -> ResponseEntity.ok(ApiResponse.Success.of(
                        ResponseCode.CREATED,
                        "Product created successfully",
                        created
                )))
                .orElse(ResponseEntity.internalServerError()
                        .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to create product")));
    }

    @Override
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(Long productId, ProductRequest request) {
        return adminProductUseCase.updateProduct(productId, request)
                .map(updated -> ResponseEntity.ok(ApiResponse.Success.of(
                        ResponseCode.OK,
                        "Product updated successfully",
                        updated
                )))
                .orElse(ResponseEntity.internalServerError()
                        .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to update product")));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteProduct(Long productId) {
        boolean success = adminProductUseCase.deleteProduct(productId);
        
        if (success) {
            return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK, "Product deleted successfully", null));
        }
        return ResponseEntity.internalServerError()
                .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to delete product"));
    }
}
