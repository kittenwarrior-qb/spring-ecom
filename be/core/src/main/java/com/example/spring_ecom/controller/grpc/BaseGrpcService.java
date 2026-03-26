package com.example.spring_ecom.controller.grpc;

import com.example.spring_ecom.grpc.common.CommonProto;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

@Slf4j
public abstract class BaseGrpcService {

    /**
     * Execute gRPC call with error handling
     */
    protected <T> void execute(String operation, GrpcCall<T> call, StreamObserver<T> observer) {
        try {
            log.info("gRPC {} called", operation);
            T response = call.execute();
            observer.onNext(response);
            observer.onCompleted();
        } catch (Exception ex) {
            log.error("Error in {} gRPC call", operation, ex);
            observer.onError(Status.INTERNAL
                    .withDescription(ex.getMessage())
                    .withCause(ex)
                    .asRuntimeException());
        }
    }

    /**
     * Build PageResponse from Spring Page
     */
    protected CommonProto.PageResponse toPageResponse(Page<?> page) {
        return CommonProto.PageResponse.newBuilder()
                .setPage(page.getNumber())
                .setSize(page.getSize())
                .setTotalElements(page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .setFirst(page.isFirst())
                .setLast(page.isLast())
                .build();
    }

    @FunctionalInterface
    protected interface GrpcCall<T> {
        T execute() throws Exception;
    }
}
