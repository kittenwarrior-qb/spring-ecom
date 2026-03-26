package com.example.spring_ecom.controller.api.review.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseModelMapper;
import com.example.spring_ecom.domain.review.ProductReview;
import com.example.spring_ecom.domain.review.ProductReviewWithUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface ProductReviewResponseMapper extends BaseModelMapper<ProductReviewResponse, ProductReview> {
    
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "adminUsername", ignore = true)
    ProductReviewResponse toResponse(ProductReview domain);
    
    ProductReviewResponse toResponse(ProductReviewWithUser domain);
}