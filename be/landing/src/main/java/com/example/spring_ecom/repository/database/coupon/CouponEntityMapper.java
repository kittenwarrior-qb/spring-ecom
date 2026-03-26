package com.example.spring_ecom.repository.database.coupon;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.coupon.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructGlobalConfig.class)
public interface CouponEntityMapper extends BaseEntityMapper<Coupon, CouponEntity> {
    
    @Override
    CouponEntity toEntity(Coupon domain);
    
    @Override
    Coupon toDomain(CouponEntity entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usedCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void update(@MappingTarget CouponEntity entity, Coupon domain);
    
    @Mapping(target = "deletedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "discountType", ignore = true)
    @Mapping(target = "discountValue", ignore = true)
    @Mapping(target = "minOrderValue", ignore = true)
    @Mapping(target = "maxDiscount", ignore = true)
    @Mapping(target = "usageLimit", ignore = true)
    @Mapping(target = "usedCount", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void markAsDeleted(@MappingTarget CouponEntity entity, Coupon ignored);
}
