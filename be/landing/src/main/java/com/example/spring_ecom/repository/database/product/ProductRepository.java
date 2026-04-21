package com.example.spring_ecom.repository.database.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.spring_ecom.repository.database.product.dao.ProductWithCategoryDao;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    
    Optional<ProductEntity> findBySlugAndDeletedAtIsNull(String slug);
    
    // Unified filtering method - can handle all combinations
    @Query("""
        SELECT p FROM ProductEntity p
        LEFT JOIN FETCH p.category c
        WHERE p.deletedAt IS NULL
        AND (:categoryId IS NULL OR p.categoryId = :categoryId)
        AND (:categorySlug IS NULL OR c.slug = :categorySlug)
        AND (:isActive IS NULL OR p.isActive = :isActive)
        AND (:isBestseller IS NULL OR p.isBestseller = :isBestseller)
    """)
    Page<ProductEntity> findProductsWithFilters(
        @Param("categoryId") Long categoryId,
        @Param("categorySlug") String categorySlug,
        @Param("isActive") Boolean isActive,
        @Param("isBestseller") Boolean isBestseller,
        Pageable pageable
    );
    
    // Keep simple ones for backward compatibility if needed
    Page<ProductEntity> findByDeletedAtIsNull(Pageable pageable);
    
    // Enhanced search with category filtering
    @Query("""
        SELECT p FROM ProductEntity p 
        LEFT JOIN CategoryEntity c ON p.categoryId = c.id
        WHERE p.deletedAt IS NULL 
        AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
             LOWER(p.author) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:categoryId IS NULL OR p.categoryId = :categoryId)
        AND (:isActive IS NULL OR p.isActive = :isActive)
    """)
    Page<ProductEntity> searchProducts(
        @Param("keyword") String keyword, 
        @Param("categoryId") Long categoryId,
        @Param("isActive") Boolean isActive,
        Pageable pageable
    );
    
    // Unified method with category info - replaces both findAllWithCategory and findByIdWithCategory
    @Query("""
        SELECT new com.example.spring_ecom.repository.database.product.dao.ProductWithCategoryDao(
               p.id, p.title, p.slug, p.author, p.publisher, p.publicationYear,
               p.language, p.pages, p.format, p.description, p.price, p.discountPrice,
               p.stockQuantity, p.coverImageUrl, p.isBestseller, p.isActive,
               p.viewCount, p.soldCount, p.ratingAverage, p.ratingCount,
               p.categoryId, c.name, p.createdAt, p.updatedAt, p.deletedAt)
        FROM ProductEntity p
        LEFT JOIN CategoryEntity c ON p.categoryId = c.id
        WHERE p.deletedAt IS NULL
        AND (:id IS NULL OR p.id = :id)
        AND (:categoryId IS NULL OR p.categoryId = :categoryId)
        AND (:isActive IS NULL OR p.isActive = :isActive)
    """)
    Page<ProductWithCategoryDao> findProductsWithCategoryInfo(
        @Param("id") Long id,
        @Param("categoryId") Long categoryId,
        @Param("isActive") Boolean isActive,
        Pageable pageable
    );
    
    // Convenience method for single product
    default Optional<ProductWithCategoryDao> findByIdWithCategory(Long id) {
        Page<ProductWithCategoryDao> result = findProductsWithCategoryInfo(id, null, null, Pageable.ofSize(1));
        return result.hasContent() ? Optional.of(result.getContent().get(0)) : Optional.empty();
    }
    
    // Convenience methods for backward compatibility
    default Page<ProductEntity> findByIsBestsellerAndDeletedAtIsNull(Boolean isBestseller, Pageable pageable) {
        return findProductsWithFilters(null, null, null, isBestseller, pageable);
    }
    
    default Page<ProductEntity> findByCategorySlug(String categorySlug, Pageable pageable) {
        return findProductsWithFilters(null, categorySlug, null, null, pageable);
    }
    
    default Page<ProductEntity> searchProducts(String keyword, Pageable pageable) {
        return searchProducts(keyword, null, null, pageable);
    }
    
    default Page<ProductWithCategoryDao> findAllWithCategory(Pageable pageable) {
        return findProductsWithCategoryInfo(null, null, null, pageable);
    }
    
    boolean existsBySlugAndDeletedAtIsNull(String slug);
}
