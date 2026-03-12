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
    
    Page<ProductEntity> findByDeletedAtIsNull(Pageable pageable);
    
    Page<ProductEntity> findByIsActiveAndDeletedAtIsNull(Boolean isActive, Pageable pageable);
    
    Page<ProductEntity> findByIsBestsellerAndDeletedAtIsNull(Boolean isBestseller, Pageable pageable);
    
    @Query("""
        SELECT p FROM ProductEntity p
        JOIN CategoryEntity c ON p.categoryId = c.id
        WHERE p.deletedAt IS NULL AND c.slug = :categorySlug
    """)
    Page<ProductEntity> findByCategorySlug(@Param("categorySlug") String categorySlug, Pageable pageable);
    
    Page<ProductEntity> findByCategoryIdAndDeletedAtIsNull(Long categoryId, Pageable pageable);
    
    @Query("""
        SELECT p FROM ProductEntity p 
        WHERE p.deletedAt IS NULL AND p.categoryId = :categoryId AND p.isActive = true
    """)
    Page<ProductEntity> findActiveByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("""
        SELECT p FROM ProductEntity p WHERE p.deletedAt IS NULL AND 
        (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
         LOWER(p.author) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    Page<ProductEntity> searchProducts(@Param("keyword") String keyword, Pageable pageable);
    
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
    """)
    Page<ProductWithCategoryDao> findAllWithCategory(Pageable pageable);
    
    @Query("""
        SELECT new com.example.spring_ecom.repository.database.product.dao.ProductWithCategoryDao(
               p.id, p.title, p.slug, p.author, p.publisher, p.publicationYear,
               p.language, p.pages, p.format, p.description, p.price, p.discountPrice,
               p.stockQuantity, p.coverImageUrl, p.isBestseller, p.isActive,
               p.viewCount, p.soldCount, p.ratingAverage, p.ratingCount,
               p.categoryId, c.name, p.createdAt, p.updatedAt, p.deletedAt)
        FROM ProductEntity p
        LEFT JOIN CategoryEntity c ON p.categoryId = c.id
        WHERE p.deletedAt IS NULL AND p.id = :id
    """)
    Optional<ProductWithCategoryDao> findByIdWithCategory(@Param("id") Long id);
    
    boolean existsBySlugAndDeletedAtIsNull(String slug);
}
