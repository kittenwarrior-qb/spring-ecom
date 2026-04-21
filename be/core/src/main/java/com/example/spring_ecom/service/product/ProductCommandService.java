package com.example.spring_ecom.service.product;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.SlugUtil;
import com.example.spring_ecom.domain.category.Category;
import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.domain.product.ProductFormat;
import com.example.spring_ecom.domain.product.StockReceiveResult;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import com.example.spring_ecom.repository.database.product.ProductEntityMapper;
import com.example.spring_ecom.repository.database.product.ProductRepository;
import com.example.spring_ecom.service.category.CategoryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCommandService {
    
    private final ProductRepository productRepository;
    private final CategoryUseCase categoryUseCase;
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

    /**
     * Nhập hàng: lock row pessimistic, cộng stock, tính giá vốn bình quân gia quyền.
     * Trả về StockReceiveResult gồm stockBefore, stockAfter, costPrice mới.
     */
    public StockReceiveResult receiveStock(Long productId, int quantityAdded, BigDecimal unitCost) {
        ProductEntity product = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Product not found: " + productId));

        int stockBefore = product.getStockQuantity();
        product.setStockQuantity(stockBefore + quantityAdded);

        // Weighted average cost price
        BigDecimal oldTotal = product.getCostPrice().multiply(BigDecimal.valueOf(stockBefore));
        BigDecimal newTotal = unitCost.multiply(BigDecimal.valueOf(quantityAdded));
        BigDecimal weightedAvg = oldTotal.add(newTotal)
                .divide(BigDecimal.valueOf(product.getStockQuantity()), 2, RoundingMode.HALF_UP);
        product.setCostPrice(weightedAvg);

        productRepository.save(product);

        return new StockReceiveResult(stockBefore, product.getStockQuantity(), weightedAvg);
    }

    /**
     * Tìm nhiều sản phẩm theo danh sách ID (chỉ trả domain, không lock).
     */
    public List<Product> findAllByIds(Collection<Long> ids) {
        return productRepository.findAllById(ids).stream()
                .filter(e -> Objects.isNull(e.getDeletedAt()))
                .map(mapper::toDomain)
                .toList();
    }

    // ========== Statistics ==========

    public Long countActiveProducts() {
        return productRepository.countActiveProducts();
    }

    public Long countLowStockProducts() {
        return productRepository.countLowStockProducts();
    }

    public Long countOutOfStockProducts() {
        return productRepository.countOutOfStockProducts();
    }

    // ========== Stock Reservation ==========

    public int reserveStock(Long productId, int quantity) {
        ProductEntity product = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Product not found: " + productId));
        int available = product.getStockQuantity() - product.getReservedQuantity();
        if (available < quantity) {
            return available; // caller checks and handles failure
        }
        product.setReservedQuantity(product.getReservedQuantity() + quantity);
        productRepository.save(product);
        return available;
    }

    public void releaseReservedStock(Long productId, int quantity) {
        ProductEntity product = findActiveProductById(productId);
        if (product.getReservedQuantity() >= quantity) {
            product.setReservedQuantity(product.getReservedQuantity() - quantity);
            productRepository.save(product);
        }
    }

    public void deductReservedStock(Long productId, int quantity) {
        ProductEntity product = findActiveProductById(productId);
        product.setStockQuantity(product.getStockQuantity() - quantity);
        product.setReservedQuantity(product.getReservedQuantity() - quantity);
        product.setSoldCount(product.getSoldCount() + quantity);
        productRepository.save(product);
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
        
        Category category = categoryUseCase.findById(categoryId)
                .orElseThrow(() -> new BaseException(ResponseCode.BAD_REQUEST, "Category not found"));
        
        if (!category.isActive()) {
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