package com.example.spring_ecom.grpc;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
public abstract class BaseGrpcClient {

    /**
     * Safely execute gRPC call returning Optional
     */
    protected <T> Optional<T> safeCall(String operation, Supplier<T> call) {
        try {
            log.info("Calling gRPC {}", operation);
            T result = call.get();
            return Optional.ofNullable(result);
        } catch (Exception ex) {
            log.error("Error calling gRPC {}", operation, ex);
            return Optional.empty();
        }
    }

    /**
     * Safely execute gRPC call returning boolean
     */
    protected boolean safeBooleanCall(String operation, Supplier<Boolean> call) {
        try {
            log.info("Calling gRPC {}", operation);
            return call.get();
        } catch (Exception ex) {
            log.error("Error calling gRPC {}", operation, ex);
            return false;
        }
    }

    /**
     * Handle null strings for proto
     */
    protected String safeString(String value) {
        return value != null ? value : "";
    }
}
