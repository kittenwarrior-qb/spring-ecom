package com.example.spring_ecom.repository.grpc.coupon;

import com.example.spring_ecom.grpc.services.CouponServiceGrpc;
import com.example.spring_ecom.grpc.services.CouponServiceProto.*;
import com.example.spring_ecom.grpc.domain.CouponProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponGrpcClient {

    @GrpcClient("core-service")
    private CouponServiceGrpc.CouponServiceBlockingStub couponServiceStub;

    public Optional<CouponValidationResult> validateCoupon(String code, BigDecimal orderTotal) {
        try {
            log.info("Validating coupon via gRPC for code: {}", code);

            ValidateCouponRequest request = ValidateCouponRequest.newBuilder()
                .setCode(code)
                .setOrderTotal(orderTotal.doubleValue())
                .build();

            ValidateCouponResponse response = couponServiceStub.validateCoupon(request);
            CouponProto.CouponValidationResult result = response.getResult();

            if (result.getValid()) {
                return Optional.of(new CouponValidationResult(
                    true,
                    result.getMessage(),
                    result.getCoupon().getId(),
                    BigDecimal.valueOf(result.getDiscountAmount())
                ));
            }

            return Optional.empty();

        } catch (Exception ex) {
            log.error("Error validating coupon via gRPC", ex);
            return Optional.empty();
        }
    }

    public boolean incrementUsage(Long couponId) {
        try {
            log.info("Incrementing coupon usage via gRPC for couponId: {}", couponId);

            IncrementUsageRequest request = IncrementUsageRequest.newBuilder()
                .setCouponId(couponId)
                .build();

            IncrementUsageResponse response = couponServiceStub.incrementUsage(request);
            return response.getSuccess();

        } catch (Exception ex) {
            log.error("Error incrementing coupon usage via gRPC", ex);
            return false;
        }
    }

    public Optional<CouponProto.Coupon> getCouponById(Long couponId) {
        try {
            log.info("Getting coupon by id via gRPC for couponId: {}", couponId);

            GetCouponByIdRequest request = GetCouponByIdRequest.newBuilder()
                .setCouponId(couponId)
                .build();

            GetCouponByIdResponse response = couponServiceStub.getCouponById(request);
            return response.getFound() ? Optional.of(response.getCoupon()) : Optional.empty();

        } catch (Exception ex) {
            log.error("Error getting coupon by id via gRPC", ex);
            return Optional.empty();
        }
    }

    public record CouponValidationResult(
        boolean valid,
        String message,
        Long couponId,
        BigDecimal discountAmount
    ) {}
}
