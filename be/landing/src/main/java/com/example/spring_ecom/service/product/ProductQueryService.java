package com.example.spring_ecom.service.product;

import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.domain.product.ProductWithCategory;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import com.example.spring_ecom.repository.database.product.ProductEntityMapper;
import com.example.spring_ecom.repository.database.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductQueryService {
    
    private final ProductRepository productRepository;
    private final ProductEntityMapper mapper;
    
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findProductsWithFilters(null, null, null, null, pageable)
                .map(mapper::toDomain);
    }
    
    public Page<ProductEntity> findAllEntities(Pageable pageable) {
        return productRepository.findProductsWithFilters(null, null, null, null, pageable);
    }
    
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id)
                .filter(entity -> Objects.isNull(entity.getDeletedAt()))
                .map(mapper::toDomain);
    }
    
    public Optional<ProductEntity> findEntityById(Long id) {
        return productRepository.findById(id)
                .filter(entity -> Objects.isNull(entity.getDeletedAt()));
    }
    
    public Optional<Product> findBySlug(String slug) {
        return productRepository.findBySlugAndDeletedAtIsNull(slug)
                .map(mapper::toDomain);
    }
    
    public Optional<ProductEntity> findEntityBySlug(String slug) {
        return productRepository.findBySlugAndDeletedAtIsNull(slug);
    }
    
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchProducts(keyword, pageable)
                .map(mapper::toDomain);
    }
    
    public Page<ProductEntity> searchProductEntities(String keyword, Pageable pageable) {
        return productRepository.searchProducts(keyword, pageable);
    }
    
    public Page<Product> findBestsellerProducts(Pageable pageable) {
        return productRepository.findByIsBestsellerAndDeletedAtIsNull(true, pageable)
                .map(mapper::toDomain);
    }
    
    public Page<ProductEntity> findBestsellerProductEntities(Pageable pageable) {
        return productRepository.findByIsBestsellerAndDeletedAtIsNull(true, pageable);
    }
    
    public Page<Product> findFeaturedProducts(Pageable pageable) {
        // Reuse bestseller for now
        return findBestsellerProducts(pageable);
    }
    
    public Page<Product> findByCategorySlug(String slug, Pageable pageable) {
        return productRepository.findByCategorySlug(slug, pageable)
                .map(mapper::toDomain);
    }
    
    public Page<ProductEntity> findEntitiesByCategorySlug(String slug, Pageable pageable) {
        return productRepository.findByCategorySlug(slug, pageable);
    }
    
    public Page<ProductWithCategory> findAllWithCategory(Pageable pageable) {
        return productRepository.findAllWithCategory(pageable)
                .map(mapper::toDomain);
    }

    // ========================== STOCK VALIDATION METHODS ==========================

    public boolean hasSufficientStock(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            ProductEntity product = productRepository.findById(cartItem.productId())
                    .orElse(null);

            if (Objects.isNull(product)) {
                return false;
            }

            int availableQuantity = product.getStockQuantity() - product.getReservedQuantity();
            if (availableQuantity < cartItem.quantity()) {
                return false;
            }
        }
        return true;
    }

    public Integer getAvailableStockQuantity(Long productId) {
        return productRepository.findById(productId)
                .map(product -> product.getStockQuantity() - product.getReservedQuantity())
                .orElse(0);
    }

    public Integer getStockQuantity(Long productId) {
        return productRepository.findById(productId)
                .map(ProductEntity::getStockQuantity)
                .orElse(0);
    }

    public List<String> getInsufficientStockProducts(List<CartItem> cartItems) {
        List<String> insufficientStockProducts = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            ProductEntity product = productRepository.findById(cartItem.productId())
                    .orElse(null);

            if (Objects.isNull(product)) {
                log.warn("Product not found in client DB: productId={}", cartItem.productId());
                insufficientStockProducts.add("Product ID " + cartItem.productId() + " not found");
                continue;
            }

            int availableQuantity = product.getStockQuantity() - product.getReservedQuantity();

            if (availableQuantity < cartItem.quantity()) {
                log.warn("Insufficient stock: productId={}, title={}, available={}, reserved={}, requested={}",
                        cartItem.productId(), product.getTitle(), availableQuantity,
                        product.getReservedQuantity(), cartItem.quantity());
                insufficientStockProducts.add(String.format("%s (available: %d, requested: %d)",
                        product.getTitle(), availableQuantity, cartItem.quantity()));
            }
        }

        return insufficientStockProducts;
    }
}
