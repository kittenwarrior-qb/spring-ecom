package com.example.spring_ecom.controller.api.coupon.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.domain.coupon.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class, componentModel = "spring")
public interface CouponResponseMapper {
    
    @Mapping(target = "remainingUses", expression = "java(calculateRemainingUses(coupon))")
    @Mapping(target = "isValid", expression = "java(coupon.isValidNow())")
    CouponResponse toResponse(Coupon coupon);
    
    default Integer calculateRemainingUses(Coupon coupon) {
        if (coupon.usageLimit() == null) {
            return null;
        }
        return Math.max(0, coupon.usageLimit() - coupon.usedCount());
    }
}
