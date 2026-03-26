package com.example.spring_ecom.service.product.admin;

import com.example.spring_ecom.controller.api.product.model.ProductRequest;
import com.example.spring_ecom.controller.api.product.model.ProductResponse;
import com.example.spring_ecom.controller.api.product.model.ProductResponseMapper;
import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.repository.grpc.product.ProductGrpcClient;
import com.example.spring_ecom.repository.grpc.product.ProductGrpcMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminProductService {

    private final ProductGrpcClient productGrpcClient;
    private final ProductGrpcMapper productGrpcMapper;
    private final ProductResponseMapper productResponseMapper;

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        log.info("Admin getting all products via gRPC: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        
        var protoProducts = productGrpcClient.getProductsAdmin(
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        List<ProductResponse> products = protoProducts.stream()
                .map(productGrpcMapper::toDomain)
                .map(productResponseMapper::toResponse)
                .toList();

        return new PageImpl<>(products, pageable, products.size());
    }

    public Optional<ProductResponse> getProductById(Long productId) {
        log.info("Admin getting product by ID via gRPC: {}", productId);
        
        return productGrpcClient.getProductById(productId)
                .map(proto -> {
                    Product product = productGrpcMapper.toDomain(proto);
                    return productResponseMapper.toResponse(product);
                });
    }

    public Optional<ProductResponse> createProduct(ProductRequest request) {
        log.info("Admin creating product via gRPC: {}", request.title());
        
        var proto = productGrpcMapper.toProto(request);
        
        return productGrpcClient.createProduct(proto)
                .map(created -> {
                    Product product = productGrpcMapper.toDomain(created);
                    return productResponseMapper.toResponse(product);
                });
    }

    public Optional<ProductResponse> updateProduct(Long productId, ProductRequest request) {
        log.info("Admin updating product via gRPC: {}", productId);
        
        var proto = productGrpcMapper.toProto(request);
        
        return productGrpcClient.updateProduct(productId, proto)
                .map(updated -> {
                    Product product = productGrpcMapper.toDomain(updated);
                    return productResponseMapper.toResponse(product);
                });
    }

    public boolean deleteProduct(Long productId) {
        log.info("Admin deleting product via gRPC: {}", productId);
        return productGrpcClient.deleteProduct(productId);
    }
}
