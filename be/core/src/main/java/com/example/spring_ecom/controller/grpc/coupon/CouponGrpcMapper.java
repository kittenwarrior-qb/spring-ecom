package com.example.spring_ecom.controller.grpc.coupon;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.grpc.domain.CouponProto;
import com.example.spring_ecom.domain.coupon.Coupon;
import com.example.spring_ecom.repository.database.coupon.DiscountType;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.Objects;

@Mapper(config = MapStructGlobalConfig.class, imports = Objects.class)
public interface CouponGrpcMapper {

    // ========== Domain -> Proto ==========

    @BeanMapping(builder = @Builder(buildMethod = "build"))
    @Mapping(target = "id", expression = "java(Objects.nonNull(coupon.id()) ? coupon.id() : 0L)")
    @Mapping(target = "code", expression = "java(nullToEmpty(coupon.code()))")
    @Mapping(target = "description", expression = "java(nullToEmpty(coupon.description()))")
    @Mapping(target = "discountType", expression = "java(toProtoDiscountType(coupon.discountType()))")
    @Mapping(target = "discountValue", expression = "java(bigDecimalToDouble(coupon.discountValue()))")
    @Mapping(target = "minOrderValue", expression = "java(bigDecimalToDouble(coupon.minOrderValue()))")
    @Mapping(target = "maxDiscount", expression = "java(bigDecimalToDoubleOrNull(coupon.maxDiscount()))")
    @Mapping(target = "usageLimit", expression = "java(Objects.nonNull(coupon.usageLimit()) ? coupon.usageLimit() : 0)")
    @Mapping(target = "usedCount", expression = "java(Objects.nonNull(coupon.usedCount()) ? coupon.usedCount() : 0)")
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "isActive", expression = "java(Objects.nonNull(coupon.isActive()) ? coupon.isActive() : false)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CouponProto.Coupon toProto(Coupon coupon);

    @BeanMapping(builder = @Builder(buildMethod = "build"))
    @Mapping(target = "valid", expression = "java(result.valid())")
    @Mapping(target = "message", expression = "java(nullToEmpty(result.message()))")
    @Mapping(target = "coupon", expression = "java(Objects.nonNull(result.coupon()) ? toProto(result.coupon()) : CouponProto.Coupon.getDefaultInstance())")
    @Mapping(target = "discountAmount", expression = "java(bigDecimalToDouble(result.discountAmount()))")
    CouponProto.CouponValidationResult toProto(CouponValidationResult result);

    // ========== Proto -> Domain ==========

    @Mapping(target = "id", expression = "java(zeroToNullLong(proto.getId()))")
    @Mapping(target = "code", expression = "java(emptyToNull(proto.getCode()))")
    @Mapping(target = "description", expression = "java(emptyToNull(proto.getDescription()))")
    @Mapping(target = "discountType", expression = "java(toDomainDiscountType(proto.getDiscountType()))")
    @Mapping(target = "discountValue", expression = "java(doubleToBigDecimal(proto.getDiscountValue()))")
    @Mapping(target = "minOrderValue", expression = "java(doubleToBigDecimal(proto.getMinOrderValue()))")
    @Mapping(target = "maxDiscount", expression = "java(doubleToBigDecimalOrNull(proto.getMaxDiscount()))")
    @Mapping(target = "usageLimit", expression = "java(zeroToNullInt(proto.getUsageLimit()))")
    @Mapping(target = "usedCount", expression = "java(proto.getUsedCount())")
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "isActive", expression = "java(proto.getIsActive())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Coupon toDomain(CouponProto.Coupon proto);

    // ========== Helper methods ==========

    @Named("zeroToNullLong")
    default Long zeroToNullLong(long value) {
        return value == 0 ? null : value;
    }

    @Named("zeroToNullInt")
    default Integer zeroToNullInt(int value) {
        return value == 0 ? null : value;
    }

    @Named("emptyToNull")
    default String emptyToNull(String value) {
        return Objects.isNull(value) || value.isEmpty() ? null : value;
    }

    @Named("nullToEmpty")
    default String nullToEmpty(String value) {
        return Objects.isNull(value) ? "" : value;
    }

    @Named("doubleToBigDecimal")
    default BigDecimal doubleToBigDecimal(double value) {
        return value == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(value);
    }

    @Named("doubleToBigDecimalOrNull")
    default BigDecimal doubleToBigDecimalOrNull(double value) {
        return value == 0 ? null : BigDecimal.valueOf(value);
    }

    @Named("bigDecimalToDouble")
    default double bigDecimalToDouble(BigDecimal value) {
        return Objects.isNull(value) ? 0 : value.doubleValue();
    }

    @Named("bigDecimalToDoubleOrNull")
    default double bigDecimalToDoubleOrNull(BigDecimal value) {
        return Objects.isNull(value) ? 0 : value.doubleValue();
    }

    @Named("toProtoDiscountType")
    default CouponProto.DiscountType toProtoDiscountType(DiscountType type) {
        if (Objects.isNull(type)) {
            return CouponProto.DiscountType.DISCOUNT_TYPE_UNSPECIFIED;
        }
        return switch (type) {
            case PERCENTAGE -> CouponProto.DiscountType.DISCOUNT_TYPE_PERCENTAGE;
            case FIXED_AMOUNT -> CouponProto.DiscountType.DISCOUNT_TYPE_FIXED_AMOUNT;
        };
    }

    @Named("toDomainDiscountType")
    default DiscountType toDomainDiscountType(CouponProto.DiscountType type) {
        if (Objects.isNull(type) || type == CouponProto.DiscountType.DISCOUNT_TYPE_UNSPECIFIED) {
            return null;
        }
        return switch (type) {
            case DISCOUNT_TYPE_PERCENTAGE -> DiscountType.PERCENTAGE;
            case DISCOUNT_TYPE_FIXED_AMOUNT -> DiscountType.FIXED_AMOUNT;
            default -> null;
        };
    }

    // Inner record for validation result
    record CouponValidationResult(
        boolean valid,
        String message,
        Coupon coupon,
        BigDecimal discountAmount
    ) {}
}
