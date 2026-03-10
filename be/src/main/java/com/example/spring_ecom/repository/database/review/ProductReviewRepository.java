package com.example.spring_ecom.repository.database.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReviewEntity, Long> {
    
    @Query("SELECT r FROM ProductReviewEntity r WHERE r.productId = :productId AND r.deletedAt IS NULL ORDER BY r.createdAt DESC")
    Page<ProductReviewEntity> findByProductId(@Param("productId") Long productId, Pageable pageable);
    
    @Query("SELECT r FROM ProductReviewEntity r WHERE r.userId = :userId AND r.deletedAt IS NULL ORDER BY r.createdAt DESC")
    Page<ProductReviewEntity> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT r FROM ProductReviewEntity r WHERE r.id = :id AND r.deletedAt IS NULL")
    Optional<ProductReviewEntity> findByIdAndNotDeleted(@Param("id") Long id);
    
    @Query("SELECT r FROM ProductReviewEntity r WHERE r.productId = :productId AND r.userId = :userId AND r.deletedAt IS NULL")
    Optional<ProductReviewEntity> findByProductIdAndUserId(@Param("productId") Long productId, @Param("userId") Long userId);
    
    @Query("SELECT AVG(r.rating) FROM ProductReviewEntity r WHERE r.productId = :productId AND r.deletedAt IS NULL")
    Double calculateAverageRating(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(r) FROM ProductReviewEntity r WHERE r.productId = :productId AND r.deletedAt IS NULL")
    Long countByProductId(@Param("productId") Long productId);
    
    boolean existsByProductIdAndUserIdAndDeletedAtIsNull(Long productId, Long userId);
}
