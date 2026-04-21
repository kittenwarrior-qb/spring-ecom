package com.example.spring_ecom.service.product;

import com.example.spring_ecom.config.MinioConfig;
import com.example.spring_ecom.controller.api.product.model.ProductResponse;
import com.example.spring_ecom.controller.api.product.model.ProductResponseMapper;
import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.domain.product.ProductWithCategory;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import com.example.spring_ecom.repository.database.product.ProductEntityMapper;
import com.example.spring_ecom.repository.database.product.ProductRepository;
import com.example.spring_ecom.service.file.FileQueryService;
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
    private final ProductResponseMapper responseMapper;
    private final FileQueryService fileQueryService;
    private final MinioConfig minioConfig;
    
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
    
    public Page<ProductWithCategory> findAllWithCategory(Long categoryId, Boolean isActive, Pageable pageable) {
        return productRepository.findProductsWithCategoryInfo(null, categoryId, isActive, pageable)
                .map(mapper::toDomain);
    }
    
    // ========== Response Mapping with Presigned URL ==========
    
    public ProductResponse toProductResponse(Product product) {
        ProductResponse response = responseMapper.toResponse(product);
        return convertCoverImageUrlToPresigned(response);
    }
    
    public ProductResponse toProductResponse(ProductEntity entity) {
        ProductResponse response = responseMapper.toResponse(entity);
        return convertCoverImageUrlToPresigned(response);
    }
    
    public ProductResponse toProductResponse(ProductWithCategory domain) {
        ProductResponse response = responseMapper.toResponse(domain);
        return convertCoverImageUrlToPresigned(response);
    }
    
    public Page<ProductResponse> toProductResponsePage(Page<Product> products) {
        return products.map(this::toProductResponse);
    }
    
    public Page<ProductResponse> toProductResponsePageFromEntities(Page<ProductEntity> entities) {
        return entities.map(this::toProductResponse);
    }
    
    public Page<ProductResponse> toProductResponsePageFromWithCategory(Page<ProductWithCategory> products) {
        return products.map(this::toProductResponse);
    }
    
    private ProductResponse convertCoverImageUrlToPresigned(ProductResponse response) {
        if (response.coverImageUrl() == null || response.coverImageUrl().isBlank()) {
            return response;
        }
        
        String presignedUrl = toPresignedUrl(response.coverImageUrl());
        return new ProductResponse(
                response.id(),
                response.title(),
                response.slug(),
                response.author(),
                response.publisher(),
                response.publicationYear(),
                response.language(),
                response.pages(),
                response.format(),
                response.description(),
                response.price(),
                response.discountPrice(),
                response.costPrice(),
                response.stockQuantity(),
                presignedUrl,
                response.isBestseller(),
                response.isActive(),
                response.viewCount(),
                response.soldCount(),
                response.ratingAverage(),
                response.ratingCount(),
                response.createdAt(),
                response.updatedAt(),
                response.categoryId(),
                response.categoryName()
        );
    }
    
    private String toPresignedUrl(String coverImageUrl) {
        String filename = coverImageUrl;
        if (coverImageUrl.startsWith("http://") || coverImageUrl.startsWith("https://")) {
            String path = coverImageUrl.split("\\?")[0];
            String[] parts = path.split("/");
            filename = parts[parts.length - 1];
        }
        
        try {
            return fileQueryService.getPresignedUrl(filename);
        } catch (Exception e) {
            log.warn("[PRODUCT-QUERY] Failed to generate presigned URL for {}: {}", filename, e.getMessage());
            return minioConfig.getEndpoint() + "/" + minioConfig.getBucket() + "/" + filename;
        }
    }
}
