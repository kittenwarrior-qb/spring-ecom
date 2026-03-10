package com.example.spring_ecom.repository.database.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewReactionRepository extends JpaRepository<ReviewReactionEntity, Long> {
    
    Optional<ReviewReactionEntity> findByReviewIdAndUserId(Long reviewId, Long userId);
    
    @Query("SELECT COUNT(r) FROM ReviewReactionEntity r WHERE r.reviewId = :reviewId AND r.reactionType = 'LIKE'")
    Long countLikesByReviewId(@Param("reviewId") Long reviewId);
    
    @Query("SELECT COUNT(r) FROM ReviewReactionEntity r WHERE r.reviewId = :reviewId AND r.reactionType = 'DISLIKE'")
    Long countDislikesByReviewId(@Param("reviewId") Long reviewId);
    
    void deleteByReviewIdAndUserId(Long reviewId, Long userId);
}
