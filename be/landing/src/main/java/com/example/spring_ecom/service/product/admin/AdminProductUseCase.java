package com.example.spring_ecom.service.product.admin;

import com.example.spring_ecom.controller.api.product.model.ProductRequest;
import com.example.spring_ecom.controller.api.product.model.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AdminProductUseCase {

    Page<ProductResponse> getAllProducts(Pageable pageable);

    Optional<ProductResponse> getProductById(Long productId);

    Optional<ProductResponse> createProduct(ProductRequest request);

    Optional<ProductResponse> updateProduct(Long productId, ProductRequest request);

    boolean deleteProduct(Long productId);
}
