package com.example.spring_ecom.controller.api.demo;

import com.example.spring_ecom.grpc.ProductGrpcClient;
import com.example.spring_ecom.grpc.UserGrpcClient;
import com.example.spring_ecom.grpc.domain.ProductProto;
import com.example.spring_ecom.grpc.domain.UserProto;
import com.example.spring_ecom.grpc.mapper.ProductGrpcMapper;
import com.example.spring_ecom.controller.api.product.model.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/api/grpc")
@RequiredArgsConstructor
public class GrpcDemoController {

    private final UserGrpcClient userGrpcClient;
    private final ProductGrpcClient productGrpcClient;
    private final ProductGrpcMapper productGrpcMapper;

    @GetMapping("/user/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable Long id) {
        log.info("gRPC: Getting user {} via gRPC", id);
        
        return userGrpcClient.getUser(id)
                .map(user -> {
                    log.info("gRPC: User found - ID: {}, Username: {}, Email: {}", 
                            user.getId(), user.getUsername(), user.getEmail());
                    return ResponseEntity.ok(Map.<String, Object>of(
                            "success", true,
                            "user", Map.of(
                                    "id", user.getId(),
                                    "username", user.getUsername(),
                                    "email", user.getEmail(),
                                    "firstName", user.getFirstName(),
                                    "lastName", user.getLastName(),
                                    "isActive", user.getIsActive()
                            ),
                            "grpcStatus", "SUCCESS"
                    ));
                })
                .orElse(ResponseEntity.ok(Map.of(
                        "success", false,
                        "error", "User not found",
                        "grpcStatus", "NOT_FOUND"
                )));
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Map<String, Object>> getProduct(@PathVariable Long id) {
        log.info("gRPC: Getting product {} via gRPC", id);
        
        return productGrpcClient.getProductById(id)
                .map(product -> {
                    log.info("gRPC: Product found - ID: {}, Title: {}, Price: {}", 
                            product.getId(), product.getTitle(), product.getPrice());
                    ProductResponse response = productGrpcMapper.toResponse(product);
                    return ResponseEntity.ok(Map.<String, Object>of(
                            "success", true,
                            "product", response,
                            "grpcStatus", "SUCCESS"
                    ));
                })
                .orElse(ResponseEntity.ok(Map.of(
                        "success", false,
                        "error", "Product not found",
                        "grpcStatus", "NOT_FOUND"
                )));
    }

}
