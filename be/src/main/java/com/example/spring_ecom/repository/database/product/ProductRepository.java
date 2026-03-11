package com.example.spring_ecom.repository.database.product;

import com.example.spring_ecom.repository.database.category.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    
    Optional<ProductEntity> findBySlugAndDeletedAtIsNull(String slug);
    
    Page<ProductEntity> findByDeletedAtIsNull(Pageable pageable);
    
    Page<ProductEntity> findByIsActiveAndDeletedAtIsNull(Boolean isActive, Pageable pageable);
    
    Page<ProductEntity> findByIsBestsellerAndDeletedAtIsNull(Boolean isBestseller, Pageable pageable);
    
    @Query("SELECT p FROM ProductEntity p WHERE p.deletedAt IS NULL AND p.category.slug = :categorySlug")
    Page<ProductEntity> findByCategorySlug(@Param("categorySlug") String categorySlug, Pageable pageable);
    
    Page<ProductEntity> findByCategoryIdAndDeletedAtIsNull(Long categoryId, Pageable pageable);
    
    Page<ProductEntity> findByCategoryAndDeletedAtIsNull(CategoryEntity category, Pageable pageable);
    
    @Query("SELECT p FROM ProductEntity p WHERE p.deletedAt IS NULL AND p.category.id = :categoryId AND p.isActive = true")
    Page<ProductEntity> findActiveByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT p FROM ProductEntity p WHERE p.deletedAt IS NULL AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.author) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ProductEntity> searchProducts(@Param("keyword") String keyword, Pageable pageable);
    
    boolean existsBySlugAndDeletedAtIsNull(String slug);
}
