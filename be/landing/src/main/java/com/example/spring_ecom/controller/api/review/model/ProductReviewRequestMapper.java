package com.example.spring_ecom.controller.api.review.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.domain.review.ProductReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface ProductReviewRequestMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "productId", source = "request.productId")
    @Mapping(target = "rating", source = "request.rating")
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "comment", source = "request.comment")
    @Mapping(target = "isVerifiedPurchase", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "dislikeCount", ignore = true)
    @Mapping(target = "adminReply", ignore = true)
    @Mapping(target = "adminReplyAt", ignore = true)
    @Mapping(target = "adminId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductReview toDomain(Long userId, CreateReviewRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "rating", source = "request.rating")
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "comment", source = "request.comment")
    @Mapping(target = "isVerifiedPurchase", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "dislikeCount", ignore = true)
    @Mapping(target = "adminReply", ignore = true)
    @Mapping(target = "adminReplyAt", ignore = true)
    @Mapping(target = "adminId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductReview toDomain(UpdateReviewRequest request);
}