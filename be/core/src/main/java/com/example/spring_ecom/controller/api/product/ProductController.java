package com.example.spring_ecom.controller.api.product;

import com.example.spring_ecom.controller.api.product.model.ProductRequest;
import com.example.spring_ecom.controller.api.product.model.ProductResponse;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.service.product.ProductQueryService;
import com.example.spring_ecom.service.product.ProductUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/api/products")
@RequiredArgsConstructor
public class ProductController implements ProductAPI {

    private final ProductUseCase productUseCase;
    private final ProductQueryService productQueryService;

    @Override
    public ApiResponse<Page<ProductResponse>> getAllProducts(Pageable pageable) {
        log.info("Getting all products with pagination");
        Page<ProductResponse> products = productQueryService.toProductResponsePageFromWithCategory(
                productUseCase.findAllWithCategory(pageable)
        );
        return ApiResponse.Success.of(products);
    }

    @Override
    public ApiResponse<ProductResponse> getProductById(Long id) {
        log.info("Getting product by ID: {}", id);
        return productUseCase.findById(id)
                .map(productQueryService::toProductResponse)
                .map(ApiResponse.Success::of)
                .orElse(null);
    }

    @Override
    public ApiResponse<ProductResponse> getProductBySlug(String slug) {
        log.info("Getting product by slug: {}", slug);
        return productUseCase.findBySlug(slug)
                .map(productQueryService::toProductResponse)
                .map(ApiResponse.Success::of)
                .orElse(null);
    }

    @Override
    public ApiResponse<Page<ProductResponse>> getProductsByCategory(String slug, Pageable pageable) {
        log.info("Getting products by category: {}", slug);
        Page<ProductResponse> products = productQueryService.toProductResponsePage(
                productUseCase.findByCategorySlug(slug, pageable)
        );
        return ApiResponse.Success.of(products);
    }

    @Override
    public ApiResponse<Page<ProductResponse>> searchProducts(String keyword, Pageable pageable) {
        log.info("Searching products with keyword: {}", keyword);
        Page<ProductResponse> products = productQueryService.toProductResponsePage(
                productUseCase.searchProducts(keyword, pageable)
        );
        return ApiResponse.Success.of(products);
    }

    @Override
    public ApiResponse<Page<ProductResponse>> getBestsellerProducts(Pageable pageable) {
        log.info("Getting bestseller products");
        Page<ProductResponse> products = productQueryService.toProductResponsePage(
                productUseCase.findBestsellerProducts(pageable)
        );
        return ApiResponse.Success.of(products);
    }

    @Override
    public ApiResponse<ProductResponse> createProduct(ProductRequest request) {
        throw new UnsupportedOperationException("Use admin API for product creation");
    }

    @Override
    public ApiResponse<ProductResponse> updateProduct(Long id, ProductRequest request) {
        throw new UnsupportedOperationException("Use admin API for product updates");
    }

    @Override
    public ApiResponse<Void> deleteProduct(Long id) {
        throw new UnsupportedOperationException("Use admin API for product deletion");
    }
}
