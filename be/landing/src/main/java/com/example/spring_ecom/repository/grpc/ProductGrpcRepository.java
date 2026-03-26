package com.example.spring_ecom.repository.grpc;

import com.example.spring_ecom.grpc.domain.ProductProto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository interface for Product gRPC operations
 * CLIENT side - calls to SERVER
 */
public interface ProductGrpcRepository {
    
    List<ProductProto.Product> getProductsAdmin(int page, int size);
    
    boolean validateProductAvailability(Long productId, Integer quantity);

    Optional<ProductProto.Product> getProductById(Long productId);

    boolean updateProductStock(Long productId, int delta);

    void updateProductsSoldCount(Map<Long, Integer> items);
    
    void incrementProductViews(Long productId);
    
    Optional<ProductProto.Product> createProduct(ProductProto.Product product);
    
    Optional<ProductProto.Product> updateProduct(Long productId, ProductProto.Product product);
    
    boolean deleteProduct(Long productId);
    
    Optional<ProductProto.Category> createCategory(ProductProto.Category category);
    
    Optional<ProductProto.Category> updateCategory(Long categoryId, ProductProto.Category category);
    
    boolean deleteCategory(Long categoryId);
    
    List<ProductProto.Category> getCategoriesAdmin(int page, int size, String search, Boolean isActive);
}