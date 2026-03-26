package com.example.spring_ecom.service.product.admin;

import com.example.spring_ecom.controller.api.product.model.ProductRequest;
import com.example.spring_ecom.controller.api.product.model.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminProductUseCaseService implements AdminProductUseCase {

    private final AdminProductQueryService queryService;
    private final AdminProductCommandService commandService;

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return queryService.getAllProducts(pageable);
    }

    @Override
    public Optional<ProductResponse> getProductById(Long productId) {
        return queryService.getProductById(productId);
    }

    @Override
    public Optional<ProductResponse> createProduct(ProductRequest request) {
        return commandService.createProduct(request);
    }

    @Override
    public Optional<ProductResponse> updateProduct(Long productId, ProductRequest request) {
        return commandService.updateProduct(productId, request);
    }

    @Override
    public boolean deleteProduct(Long productId) {
        return commandService.deleteProduct(productId);
    }
}
