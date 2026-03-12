package com.example.spring_ecom.repository.database.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewReactionRepository extends JpaRepository<ReviewReactionEntity, Long> {
    
    Optional<ReviewReactionEntity> findByReviewIdAndUserId(Long reviewId, Long userId);
    
    // Unified method for counting reactions by type
    @Query("SELECT COUNT(r) FROM ReviewReactionEntity r WHERE r.reviewId = :reviewId AND (:reactionType IS NULL OR r.reactionType = :reactionType)")
    Long countReactionsByType(@Param("reviewId") Long reviewId, @Param("reactionType") String reactionType);
    
    // Convenience methods for backward compatibility
    default Long countLikesByReviewId(Long reviewId) {
        return countReactionsByType(reviewId, "LIKE");
    }
    
    default Long countDislikesByReviewId(Long reviewId) {
        return countReactionsByType(reviewId, "DISLIKE");
    }
    
    void deleteByReviewIdAndUserId(Long reviewId, Long userId);
}
