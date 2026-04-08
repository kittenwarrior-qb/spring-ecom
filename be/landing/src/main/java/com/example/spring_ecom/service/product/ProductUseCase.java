package com.example.spring_ecom.service.product;

import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.domain.product.ProductWithCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductUseCase {

    Page<Product> findAll(Pageable pageable);

    Page<ProductWithCategory> findAllWithCategory(Pageable pageable);

    Optional<Product> findById(Long id);

    Optional<Product> findBySlug(String slug);

    Page<Product> findByCategorySlug(String slug, Pageable pageable);

    Page<Product> searchProducts(String keyword, Pageable pageable);

    Page<Product> findBestsellerProducts(Pageable pageable);

    Page<Product> findFeaturedProducts(Pageable pageable);

    Optional<Product> create(Product product);

    Optional<Product> update(Long id, Product product);

    void delete(Long id);

    void validateStockForOrder(List<CartItem> cartItems);
}
