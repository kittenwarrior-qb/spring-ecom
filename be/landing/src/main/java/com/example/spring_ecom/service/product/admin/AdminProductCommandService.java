package com.example.spring_ecom.service.product.admin;

import com.example.spring_ecom.controller.api.product.model.ProductRequest;
import com.example.spring_ecom.controller.api.product.model.ProductResponse;
import com.example.spring_ecom.controller.api.product.model.ProductResponseMapper;
import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.repository.grpc.product.ProductGrpcClient;
import com.example.spring_ecom.repository.grpc.product.ProductGrpcMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminProductCommandService {

    private final ProductGrpcClient productGrpcClient;
    private final ProductGrpcMapper productGrpcMapper;
    private final ProductResponseMapper productResponseMapper;

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
