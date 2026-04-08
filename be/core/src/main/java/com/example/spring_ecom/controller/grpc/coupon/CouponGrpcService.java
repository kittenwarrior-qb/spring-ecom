package com.example.spring_ecom.controller.grpc.coupon;

import com.example.spring_ecom.grpc.services.CouponServiceGrpc;
import com.example.spring_ecom.grpc.services.CouponServiceProto.*;
import com.example.spring_ecom.grpc.domain.CouponProto;
import com.example.spring_ecom.service.coupon.CouponUseCase;
import com.example.spring_ecom.domain.coupon.Coupon;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CouponGrpcService extends CouponServiceGrpc.CouponServiceImplBase {

    private final CouponUseCase couponUseCase;
    private final CouponGrpcMapper mapper;

    @Override
    public void validateCoupon(ValidateCouponRequest request, StreamObserver<ValidateCouponResponse> responseObserver) {
        try {
            log.info("gRPC ValidateCoupon called for code: {}", request.getCode());

            var result = couponUseCase.validateCoupon(request.getCode(), BigDecimal.valueOf(request.getOrderTotal()));

            CouponGrpcMapper.CouponValidationResult mapperResult = new CouponGrpcMapper.CouponValidationResult(
                result.isPresent(),
                result.isPresent() ? "Coupon is valid" : "Invalid coupon",
                result.map(CouponUseCase.CouponValidationResult::coupon).orElse(null),
                result.map(CouponUseCase.CouponValidationResult::discountAmount).orElse(BigDecimal.ZERO)
            );

            ValidateCouponResponse response = ValidateCouponResponse.newBuilder()
                .setResult(mapper.toProto(mapperResult))
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception ex) {
            log.error("Error in ValidateCoupon gRPC call", ex);

            CouponGrpcMapper.CouponValidationResult errorResult = new CouponGrpcMapper.CouponValidationResult(
                false,
                "Error: " + ex.getMessage(),
                null,
                BigDecimal.ZERO
            );

            ValidateCouponResponse response = ValidateCouponResponse.newBuilder()
                .setResult(mapper.toProto(errorResult))
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void incrementUsage(IncrementUsageRequest request, StreamObserver<IncrementUsageResponse> responseObserver) {
        try {
            log.info("gRPC IncrementUsage called for couponId: {}", request.getCouponId());

            couponUseCase.incrementUsage(request.getCouponId());

            IncrementUsageResponse response = IncrementUsageResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Usage incremented successfully")
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception ex) {
            log.error("Error in IncrementUsage gRPC call", ex);

            IncrementUsageResponse response = IncrementUsageResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Failed: " + ex.getMessage())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getCouponById(GetCouponByIdRequest request, StreamObserver<GetCouponByIdResponse> responseObserver) {
        try {
            log.info("gRPC GetCouponById called for couponId: {}", request.getCouponId());

            java.util.Optional<Coupon> coupon = couponUseCase.findById(request.getCouponId());

            if (coupon.isPresent()) {
                GetCouponByIdResponse response = GetCouponByIdResponse.newBuilder()
                    .setCoupon(mapper.toProto(coupon.get()))
                    .setFound(true)
                    .build();
                responseObserver.onNext(response);
            } else {
                GetCouponByIdResponse response = GetCouponByIdResponse.newBuilder()
                    .setFound(false)
                    .build();
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();

        } catch (Exception ex) {
            log.error("Error in GetCouponById gRPC call", ex);
            responseObserver.onNext(GetCouponByIdResponse.newBuilder().setFound(false).build());
            responseObserver.onCompleted();
        }
    }
}
