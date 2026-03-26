package com.example.spring_ecom.service.product;

import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.domain.product.ProductWithCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductUseCase {
    
    Page<Product> findAll(Pageable pageable);
    
    Page<ProductWithCategory> findAllWithCategory(Pageable pageable);
    
    Optional<Product> findById(Long id);
    
    Optional<Product> findBySlug(String slug);
    
    Page<Product> findByCategorySlug(String slug, Pageable pageable);
    
    Page<Product> searchProducts(String keyword, Pageable pageable);
    
    Page<Product> findBestsellerProducts(Pageable pageable);
    
    Optional<Product> create(Product product);
    
    Optional<Product> update(Long id, Product product);
    
    void delete(Long id);
    
    /**
     * Update tồn kho sản phẩm. delta < 0 để trừ, delta > 0 để cộng.
     * @return stock mới sau khi update
     */
    int updateProductStock(Long productId, int delta);
    
    /**
     * Cập nhật soldCount cho nhiều sản phẩm cùng lúc (dùng khi giao hàng thành công).
     * items: map productId -> quantity sold
     */
    void updateProductsSoldCount(java.util.Map<Long, Integer> items);
}
