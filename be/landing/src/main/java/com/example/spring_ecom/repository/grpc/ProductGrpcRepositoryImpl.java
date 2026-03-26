package com.example.spring_ecom.repository.grpc;

import com.example.spring_ecom.grpc.ProductGrpcClient;
import com.example.spring_ecom.grpc.domain.ProductProto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductGrpcRepositoryImpl implements ProductGrpcRepository {
    
    private final ProductGrpcClient productGrpcClient;
    
    @Override
    public List<ProductProto.Product> getProductsAdmin(int page, int size) {
        return productGrpcClient.getProductsAdmin(page, size);
    }
    
    @Override
    public boolean validateProductAvailability(Long productId, Integer quantity) {
        return productGrpcClient.validateProductAvailability(productId, quantity);
    }
    
    @Override
    public Optional<ProductProto.Product> getProductById(Long productId) {
        return productGrpcClient.getProductById(productId);
    }
    
    @Override
    public boolean updateProductStock(Long productId, int delta) {
        return productGrpcClient.updateProductStock(productId, delta);
    }
    
    @Override
    public void updateProductsSoldCount(java.util.Map<Long, Integer> items) {
        productGrpcClient.updateProductsSoldCount(items);
    }
    
    @Override
    public void incrementProductViews(Long productId) {
        productGrpcClient.incrementProductViews(productId);
    }
    
    @Override
    public Optional<ProductProto.Product> createProduct(ProductProto.Product product) {
        return productGrpcClient.createProduct(product);
    }
    
    @Override
    public Optional<ProductProto.Product> updateProduct(Long productId, ProductProto.Product product) {
        return productGrpcClient.updateProduct(productId, product);
    }
    
    @Override
    public boolean deleteProduct(Long productId) {
        return productGrpcClient.deleteProduct(productId);
    }
    
    @Override
    public Optional<ProductProto.Category> createCategory(ProductProto.Category category) {
        return productGrpcClient.createCategory(category);
    }
    
    @Override
    public Optional<ProductProto.Category> updateCategory(Long categoryId, ProductProto.Category category) {
        return productGrpcClient.updateCategory(categoryId, category);
    }
    
    @Override
    public boolean deleteCategory(Long categoryId) {
        return productGrpcClient.deleteCategory(categoryId);
    }
    
    @Override
    public List<ProductProto.Category> getCategoriesAdmin(int page, int size, String search, Boolean isActive) {
        return productGrpcClient.getCategoriesAdmin(page, size, search, isActive);
    }
}