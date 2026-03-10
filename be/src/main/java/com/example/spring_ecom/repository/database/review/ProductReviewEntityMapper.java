package com.example.spring_ecom.repository.database.review;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.review.ProductReview;
import org.mapstruct.Mapper;

@Mapper(config = MapStructGlobalConfig.class)
public interface ProductReviewEntityMapper extends BaseEntityMapper<ProductReview, ProductReviewEntity> {
    // Các method từ BaseEntityMapper đã có sẵn, không cần khai báo lại
}
