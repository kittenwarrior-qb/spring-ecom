package com.example.spring_ecom.repository.grpc;

import com.example.spring_ecom.grpc.domain.OrderProto;
import com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order gRPC operations
 * CLIENT side - calls to SERVER
 */
public interface OrderGrpcRepository {
    
    List<OrderProto.Order> getOrders(int page, int size, String search, String status, 
                                    String paymentStatus, LocalDate dateFrom, LocalDate dateTo);
    
    Optional<OrderProto.Order> getOrderDetail(Long orderId);
    
    Optional<OrderProto.Order> updateOrderStatus(Long orderId, String status);
    
    Optional<GetOrderStatisticsResponse> getOrderStatistics(String period, LocalDate dateFrom, LocalDate dateTo);
    
    boolean validateOrderExists(Long orderId);
}