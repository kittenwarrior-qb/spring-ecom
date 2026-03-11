package com.example.spring_ecom.controller.api.product;

import com.example.spring_ecom.controller.api.product.model.ProductRequest;
import com.example.spring_ecom.controller.api.product.model.ProductRequestMapper;
import com.example.spring_ecom.controller.api.product.model.ProductResponse;
import com.example.spring_ecom.controller.api.product.model.ProductResponseMapper;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.service.product.ProductQueryService;
import com.example.spring_ecom.service.product.ProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductAPI {
    
    private final ProductUseCase productUseCase;
    private final ProductQueryService productQueryService;
    private final ProductRequestMapper requestMapper;
    private final ProductResponseMapper responseMapper;
    
    @Override
    public ApiResponse<Page<ProductResponse>> getAllProducts(Pageable pageable) {
        Page<ProductResponse> products = productQueryService.findAllEntities(pageable)
                .map(responseMapper::fromEntity);
        return ApiResponse.Success.of(products);
    }
    
    @Override
    public ApiResponse<ProductResponse> getProductById(Long id) {
        ProductResponse product = productQueryService.findEntityById(id)
                .map(responseMapper::fromEntity)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Product not found"));
        return ApiResponse.Success.of(product);
    }
    
    @Override
    public ApiResponse<ProductResponse> getProductBySlug(String slug) {
        ProductResponse product = productQueryService.findEntityBySlug(slug)
                .map(responseMapper::fromEntity)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Product not found"));
        return ApiResponse.Success.of(product);
    }
    
    @Override
    public ApiResponse<Page<ProductResponse>> getProductsByCategory(String slug, Pageable pageable) {
        Page<ProductResponse> products = productQueryService.findEntitiesByCategorySlug(slug, pageable)
                .map(responseMapper::fromEntity);
        return ApiResponse.Success.of(products);
    }
    
    @Override
    public ApiResponse<Page<ProductResponse>> searchProducts(String keyword, Pageable pageable) {
        Page<ProductResponse> products = productQueryService.searchProductEntities(keyword, pageable)
                .map(responseMapper::fromEntity);
        return ApiResponse.Success.of(products);
    }
    
    @Override
    public ApiResponse<Page<ProductResponse>> getBestsellerProducts(Pageable pageable) {
        Page<ProductResponse> products = productQueryService.findBestsellerProductEntities(pageable)
                .map(responseMapper::fromEntity);
        return ApiResponse.Success.of(products);
    }
    
    @Override
    public ApiResponse<ProductResponse> createProduct(ProductRequest request) {
        ProductResponse product = productUseCase.create(requestMapper.toDomain(request))
                .flatMap(p -> productQueryService.findEntityById(p.id()))
                .map(responseMapper::fromEntity)
                .orElseThrow(() -> new BaseException(ResponseCode.BAD_REQUEST, "Failed to create product"));
        return ApiResponse.Success.of(product);
    }
    
    @Override
    public ApiResponse<ProductResponse> updateProduct(Long id, ProductRequest request) {
        ProductResponse product = productUseCase.update(id, requestMapper.toDomain(request))
                .flatMap(p -> productQueryService.findEntityById(p.id()))
                .map(responseMapper::fromEntity)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Product not found"));
        return ApiResponse.Success.of(product);
    }
    
    @Override
    public ApiResponse<Void> deleteProduct(Long id) {
        productUseCase.delete(id);
        return ApiResponse.Success.of(ResponseCode.OK, "Product deleted successfully", null);
    }
}
