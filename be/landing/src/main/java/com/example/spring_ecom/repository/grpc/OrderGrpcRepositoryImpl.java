package com.example.spring_ecom.repository.grpc;

import com.example.spring_ecom.grpc.OrderGrpcClient;
import com.example.spring_ecom.grpc.domain.OrderProto;
import com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderGrpcRepositoryImpl implements OrderGrpcRepository {
    
    private final OrderGrpcClient orderGrpcClient;
    
    @Override
    public List<OrderProto.Order> getOrders(int page, int size, String search, String status, 
                                           String paymentStatus, LocalDate dateFrom, LocalDate dateTo) {
        return orderGrpcClient.getOrders(page, size, search, status, paymentStatus, dateFrom, dateTo);
    }
    
    @Override
    public Optional<OrderProto.Order> getOrderDetail(Long orderId) {
        return orderGrpcClient.getOrderDetail(orderId);
    }
    
    @Override
    public Optional<OrderProto.Order> updateOrderStatus(Long orderId, String status) {
        return orderGrpcClient.updateOrderStatus(orderId, status);
    }
    
    @Override
    public Optional<GetOrderStatisticsResponse> getOrderStatistics(String period, LocalDate dateFrom, LocalDate dateTo) {
        return orderGrpcClient.getOrderStatistics(period, dateFrom, dateTo);
    }
    
    @Override
    public boolean validateOrderExists(Long orderId) {
        return orderGrpcClient.validateOrderExists(orderId);
    }
}