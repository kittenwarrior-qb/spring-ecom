package com.example.spring_ecom.service.product;

import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import com.example.spring_ecom.repository.database.product.ProductEntityMapper;
import com.example.spring_ecom.repository.database.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductQueryService {
    
    private final ProductRepository productRepository;
    private final ProductEntityMapper mapper;
    
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findByDeletedAtIsNull(pageable)
                .map(mapper::toDomain);
    }
    
    public Page<ProductEntity> findAllEntities(Pageable pageable) {
        return productRepository.findByDeletedAtIsNull(pageable);
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
    
    public Page<Product> findByCategorySlug(String slug, Pageable pageable) {
        return productRepository.findByCategorySlug(slug, pageable)
                .map(mapper::toDomain);
    }
    
    public Page<ProductEntity> findEntitiesByCategorySlug(String slug, Pageable pageable) {
        return productRepository.findByCategorySlug(slug, pageable);
    }
}
