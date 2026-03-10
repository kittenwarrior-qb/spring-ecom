package com.example.spring_ecom.repository.database.review;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.review.ProductReview;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructGlobalConfig.class)
public interface ProductReviewEntityMapper extends BaseEntityMapper<ProductReview, ProductReviewEntity> {
    
    ProductReviewEntity toEntity(ProductReview source);
    
    void update(@MappingTarget ProductReviewEntity target, ProductReview source);
}
