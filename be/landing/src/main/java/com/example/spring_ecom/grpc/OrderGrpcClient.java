package com.example.spring_ecom.grpc;

import com.example.spring_ecom.grpc.services.OrderServiceGrpc;
import com.example.spring_ecom.grpc.services.OrderServiceProto.*;
import com.example.spring_ecom.grpc.domain.OrderProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderGrpcClient {
    
    @GrpcClient("core-service")
    private OrderServiceGrpc.OrderServiceBlockingStub orderServiceStub;
    
    public List<OrderProto.Order> getOrders(int page, int size, String search, String status, 
                                           String paymentStatus, LocalDate dateFrom, LocalDate dateTo) {
        try {
            log.info("Calling gRPC GetOrders with page: {}, size: {}", page, size);
            
            GetOrdersRequest.Builder requestBuilder = GetOrdersRequest.newBuilder()
                    .setPageRequest(
                            com.example.spring_ecom.grpc.common.CommonProto.PageRequest.newBuilder()
                                    .setPage(page)
                                    .setSize(size)
                                    .build()
                    );
            
            if (search != null && !search.isEmpty()) {
                requestBuilder.setSearch(search);
            }
            if (status != null && !status.isEmpty()) {
                requestBuilder.setStatus(status);
            }
            if (paymentStatus != null && !paymentStatus.isEmpty()) {
                requestBuilder.setPaymentStatus(paymentStatus);
            }
            if (dateFrom != null) {
                requestBuilder.setDateFrom(dateFrom.toString());
            }
            if (dateTo != null) {
                requestBuilder.setDateTo(dateTo.toString());
            }
            
            GetOrdersResponse response = orderServiceStub.getOrders(requestBuilder.build());
            return response.getOrdersList();
            
        } catch (Exception ex) {
            log.error("Error calling gRPC GetOrders", ex);
            return List.of();
        }
    }
    
    public Optional<OrderProto.Order> getOrderDetail(Long orderId) {
        try {
            log.info("Calling gRPC GetOrderDetail for orderId: {}", orderId);
            
            GetOrderDetailRequest request = GetOrderDetailRequest.newBuilder()
                    .setOrderId(orderId)
                    .build();
            
            GetOrderDetailResponse response = orderServiceStub.getOrderDetail(request);
            return Optional.of(response.getOrder());
            
        } catch (Exception ex) {
            log.error("Error calling gRPC GetOrderDetail", ex);
            return Optional.empty();
        }
    }
    
    public Optional<OrderProto.Order> updateOrderStatus(Long orderId, String status) {
        try {
            log.info("Calling gRPC UpdateOrderStatus for orderId: {}, status: {}", orderId, status);
            
            UpdateOrderStatusRequest request = UpdateOrderStatusRequest.newBuilder()
                    .setOrderId(orderId)
                    .setStatus(status)
                    .build();
            
            UpdateOrderStatusResponse response = orderServiceStub.updateOrderStatus(request);
            return Optional.of(response.getOrder());
            
        } catch (Exception ex) {
            log.error("Error calling gRPC UpdateOrderStatus", ex);
            return Optional.empty();
        }
    }
    
    public Optional<GetOrderStatisticsResponse> getOrderStatistics(String period, LocalDate dateFrom, LocalDate dateTo) {
        try {
            log.info("Calling gRPC GetOrderStatistics with period: {}", period);
            
            GetOrderStatisticsRequest.Builder requestBuilder = GetOrderStatisticsRequest.newBuilder()
                    .setPeriod(period);
            
            if (dateFrom != null) {
                requestBuilder.setDateFrom(dateFrom.toString());
            }
            if (dateTo != null) {
                requestBuilder.setDateTo(dateTo.toString());
            }
            
            GetOrderStatisticsResponse response = orderServiceStub.getOrderStatistics(requestBuilder.build());
            return Optional.of(response);
            
        } catch (Exception ex) {
            log.error("Error calling gRPC GetOrderStatistics", ex);
            return Optional.empty();
        }
    }
    
    public boolean validateOrderExists(Long orderId) {
        try {
            log.info("Validating order existence via gRPC for orderId: {}", orderId);
            
            Optional<OrderProto.Order> order = getOrderDetail(orderId);
            return order.isPresent();
            
        } catch (Exception ex) {
            log.error("Error validating order existence via gRPC", ex);
            return false;
        }
    }
}