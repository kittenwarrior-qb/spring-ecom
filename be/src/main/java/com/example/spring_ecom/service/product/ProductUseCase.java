package com.example.spring_ecom.service.product;

import com.example.spring_ecom.domain.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductUseCase {
    
    Page<Product> findAll(Pageable pageable);
    
    Optional<Product> findById(Long id);
    
    Optional<Product> findBySlug(String slug);
    
    Page<Product> searchProducts(String keyword, Pageable pageable);
    
    Page<Product> findBestsellerProducts(Pageable pageable);
    
    Optional<Product> create(Product product);
    
    Optional<Product> update(Long id, Product product);
    
    void delete(Long id);
}
