package com.example.spring_ecom.repository.database.review;

import com.example.spring_ecom.repository.database.common.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReviewEntity extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Integer rating;
    
    @Column(length = 255)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String comment;
    
    @Column(name = "is_verified_purchase", nullable = false)
    @Builder.Default
    private Boolean isVerifiedPurchase = false;
    
    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private Integer likeCount = 0;
    
    @Column(name = "dislike_count", nullable = false)
    @Builder.Default
    private Integer dislikeCount = 0;
    
    @Column(name = "admin_reply", columnDefinition = "TEXT")
    private String adminReply;
    
    @Column(name = "admin_reply_at")
    private LocalDateTime adminReplyAt;
    
    @Column(name = "admin_id")
    private Long adminId;
}
