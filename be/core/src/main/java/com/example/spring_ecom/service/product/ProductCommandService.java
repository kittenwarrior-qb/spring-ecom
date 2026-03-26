package com.example.spring_ecom.service.product;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.SlugUtil;
import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.domain.product.ProductFormat;
import com.example.spring_ecom.repository.database.category.CategoryEntity;
import com.example.spring_ecom.repository.database.category.CategoryRepository;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import com.example.spring_ecom.repository.database.product.ProductEntityMapper;
import com.example.spring_ecom.repository.database.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCommandService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductEntityMapper mapper;
    
    // ========================== MAIN METHODS ================================

    public Optional<Product> create(Product product) {
        validateProduct(product);
        validateCategory(product.categoryId());
        
        ProductEntity entity = mapper.toEntity(product);
        handleSlugGeneration(entity, null);
        
        productRepository.save(entity);
        return Optional.of(mapper.toDomain(entity));
    }
    
    public Optional<Product> update(Long id, Product product) {
        ProductEntity entity = findActiveProductById(id);
        
        validateProduct(product);
        validateCategory(product.categoryId());
        validateProductFormat(product.format());
        handleSlugUpdate(product.slug(), entity.getSlug());
        
        mapper.update(entity, product);
        
        productRepository.save(entity);
        return Optional.of(mapper.toDomain(entity));
    }
    
    public void delete(Long id) {
        ProductEntity entity = findActiveProductById(id);
        mapper.markAsDeleted(entity, null);
        productRepository.save(entity);
    }
    
    public int updateProductStock(Long productId, int delta) {
        ProductEntity entity = findActiveProductById(productId);
        int newStock = entity.getStockQuantity() + delta;
        if (newStock < 0) {
            throw new com.example.spring_ecom.core.exception.BaseException(
                com.example.spring_ecom.core.response.ResponseCode.BAD_REQUEST,
                "Insufficient stock for product: " + entity.getTitle() + ". Available: " + entity.getStockQuantity()
            );
        }
        entity.setStockQuantity(newStock);
        productRepository.save(entity);
        return newStock;
    }
    
    public void updateProductsSoldCount(java.util.Map<Long, Integer> items) {
        if (Objects.isNull(items) || items.isEmpty()) return;
        List<ProductEntity> products = productRepository.findAllById(items.keySet());
        products.forEach(product -> {
            Integer qty = items.get(product.getId());
            if (Objects.nonNull(qty) && qty > 0) {
                product.setSoldCount(product.getSoldCount() + qty);
            }
        });
        productRepository.saveAll(products);
    }




    // ========================== SUPPORT METHODS ================================

    private ProductEntity findActiveProductById(Long id) {
        return productRepository.findById(id)
                .filter(e -> Objects.isNull(e.getDeletedAt()))
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Product not found"));
    }
    
    private void validateProduct(Product product) {
        if (Objects.nonNull(product.discountPrice()) && 
            product.discountPrice().compareTo(product.price()) > 0) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Discount price cannot be greater than price");
        }
    }
    
    private void validateCategory(Long categoryId) {
        if (Objects.isNull(categoryId)) return;
        
        CategoryEntity category = categoryRepository.findById(categoryId)
                .filter(c -> Objects.isNull(c.getDeletedAt()))
                .orElseThrow(() -> new BaseException(ResponseCode.BAD_REQUEST, "Category not found"));
        
        if (!category.getIsActive()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Category is not active");
        }
    }
    
    private void validateProductFormat(String format) {
        if (Objects.isNull(format) || format.isBlank()) return;
        
        try {
            ProductFormat.fromString(format);
        } catch (IllegalArgumentException e) {
            throw new BaseException(ResponseCode.BAD_REQUEST, e.getMessage());
        }
    }
    
    private void handleSlugGeneration(ProductEntity entity, String currentSlug) {
        if (Objects.isNull(entity.getSlug()) || entity.getSlug().isBlank()) {
            String baseSlug = SlugUtil.toSlug(entity.getTitle());
            String uniqueSlug = generateUniqueSlug(baseSlug);
            mapper.updateSlug(entity, uniqueSlug);
        } else {
            validateSlugUniqueness(entity.getSlug());
        }
    }
    
    private void handleSlugUpdate(String newSlug, String currentSlug) {
        if (Objects.nonNull(newSlug) && !newSlug.equals(currentSlug)) {
            validateSlugUniqueness(newSlug);
        }
    }
    
    private void validateSlugUniqueness(String slug) {
        if (productRepository.existsBySlugAndDeletedAtIsNull(slug)) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Product slug already exists");
        }
    }
    
    private String generateUniqueSlug(String baseSlug) {
        String slug = baseSlug;
        int suffix = 0;
        int maxRetries = 100; // Prevent infinite loop
        
        while (suffix < maxRetries && productRepository.existsBySlugAndDeletedAtIsNull(slug)) {
            suffix++;
            slug = SlugUtil.toSlugWithSuffix(baseSlug, suffix);
        }
        
        if (suffix >= maxRetries) {
            // Fallback with timestamp
            slug = baseSlug + "-" + System.currentTimeMillis();
        }
        
        return slug;
    }
}