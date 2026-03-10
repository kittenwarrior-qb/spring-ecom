package com.example.spring_ecom.service.product;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.SlugUtil;
import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.domain.product.ProductFormat;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import com.example.spring_ecom.repository.database.product.ProductEntityMapper;
import com.example.spring_ecom.repository.database.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCommandService {
    
    private final ProductRepository productRepository;
    private final ProductEntityMapper mapper;
    
    public Optional<Product> create(Product product) {
        if (product.discountPrice() != null && 
            product.discountPrice().compareTo(product.price()) > 0) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Discount price cannot be greater than price");
        }
        
        ProductEntity entity = mapper.toEntity(product);
        
        if (entity.getFormat() != null && !entity.getFormat().isBlank()) {
            try {
                ProductFormat.fromString(entity.getFormat());
            } catch (IllegalArgumentException e) {
                throw new BaseException(ResponseCode.BAD_REQUEST, e.getMessage());
            }
        }
        
        if (entity.getSlug() == null || entity.getSlug().isBlank()) {
            String baseSlug = SlugUtil.toSlug(entity.getTitle());
            String uniqueSlug = generateUniqueSlug(baseSlug);
            entity.setSlug(uniqueSlug);
        } else {
            if (productRepository.existsBySlugAndDeletedAtIsNull(entity.getSlug())) {
                throw new BaseException(ResponseCode.BAD_REQUEST, "Product slug already exists");
            }
        }
        
        if (entity.getLanguage() == null || entity.getLanguage().isBlank()) {
            entity.setLanguage("Vietnamese");
        }
        if (entity.getFormat() == null || entity.getFormat().isBlank()) {
            entity.setFormat("Paperback");
        }
        if (entity.getStockQuantity() == null) {
            entity.setStockQuantity(0);
        }
        if (entity.getIsBestseller() == null) {
            entity.setIsBestseller(false);
        }
        if (entity.getIsActive() == null) {
            entity.setIsActive(true);
        }
        if (entity.getViewCount() == null) {
            entity.setViewCount(0);
        }
        if (entity.getSoldCount() == null) {
            entity.setSoldCount(0);
        }
        if (entity.getRatingAverage() == null) {
            entity.setRatingAverage(BigDecimal.ZERO);
        }
        if (entity.getRatingCount() == null) {
            entity.setRatingCount(0);
        }
        
        ProductEntity saved = productRepository.save(entity);
        return Optional.of(mapper.toDomain(saved));
    }
    
    public Optional<Product> update(Long id, Product product) {
        ProductEntity entity = productRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Product not found"));
        
        if (product.format() != null && !product.format().isBlank()) {
            try {
                ProductFormat.fromString(product.format());
            } catch (IllegalArgumentException e) {
                throw new BaseException(ResponseCode.BAD_REQUEST, e.getMessage());
            }
        }
        
        if (!entity.getSlug().equals(product.slug()) && 
            productRepository.existsBySlugAndDeletedAtIsNull(product.slug())) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Product slug already exists");
        }
        
        if (product.discountPrice() != null && 
            product.discountPrice().compareTo(product.price()) > 0) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Discount price cannot be greater than price");
        }
        
        mapper.update(entity, product);
        
        ProductEntity updated = productRepository.save(entity);
        return Optional.of(mapper.toDomain(updated));
    }
    
    public void delete(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Product not found"));
        
        entity.setDeletedAt(LocalDateTime.now());
        productRepository.save(entity);
    }

    private String generateUniqueSlug(String baseSlug) {
        String slug = baseSlug;
        int suffix = 0;
        
        while (productRepository.existsBySlugAndDeletedAtIsNull(slug)) {
            suffix++;
            slug = SlugUtil.toSlugWithSuffix(baseSlug, suffix);
        }
        
        return slug;
    }
}
