package com.example.spring_ecom.controller.grpc;

import com.example.spring_ecom.grpc.services.OrderServiceGrpc;
import com.example.spring_ecom.grpc.services.OrderServiceProto.*;
import com.example.spring_ecom.grpc.domain.OrderProto;
import com.example.spring_ecom.service.order.OrderUseCase;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderStatus;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {
    
    private final OrderUseCase orderUseCase;
    private final OrderGrpcMapper orderGrpcMapper;
    
    @Override
    public void getOrders(GetOrdersRequest request, StreamObserver<GetOrdersResponse> responseObserver) {
        try {
            log.info("gRPC GetOrders called with page: {}, size: {}", 
                    request.getPageRequest().getPage(), request.getPageRequest().getSize());
            
            Pageable pageable = PageRequest.of(
                    request.getPageRequest().getPage(),
                    request.getPageRequest().getSize()
            );
            
            // Build filter criteria
            String search = request.getSearch().isEmpty() ? null : request.getSearch();
            String status = request.getStatus().isEmpty() ? null : request.getStatus();
            String paymentStatus = request.getPaymentStatus().isEmpty() ? null : request.getPaymentStatus();
            LocalDate dateFrom = request.getDateFrom().isEmpty() ? null : 
                    LocalDate.parse(request.getDateFrom(), DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate dateTo = request.getDateTo().isEmpty() ? null : 
                    LocalDate.parse(request.getDateTo(), DateTimeFormatter.ISO_LOCAL_DATE);
            
            Page<Order> orderPage = orderUseCase.findAllWithFilters(
                    pageable, search, status, paymentStatus, dateFrom, dateTo);
            
            GetOrdersResponse.Builder responseBuilder = GetOrdersResponse.newBuilder();
            
            // Add orders
            orderPage.getContent().forEach(order -> {
                OrderProto.Order orderProto = orderGrpcMapper.toProto(order);
                responseBuilder.addOrders(orderProto);
            });
            
            // Add page info
            responseBuilder.setPageResponse(
                    com.example.spring_ecom.grpc.common.CommonProto.PageResponse.newBuilder()
                            .setPage(orderPage.getNumber())
                            .setSize(orderPage.getSize())
                            .setTotalElements(orderPage.getTotalElements())
                            .setTotalPages(orderPage.getTotalPages())
                            .setFirst(orderPage.isFirst())
                            .setLast(orderPage.isLast())
                            .build()
            );
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            
        } catch (Exception ex) {
            log.error("Error in GetOrders gRPC call", ex);
            responseObserver.onError(ex);
        }
    }
    
    @Override
    public void getOrderDetail(GetOrderDetailRequest request, StreamObserver<GetOrderDetailResponse> responseObserver) {
        try {
            log.info("gRPC GetOrderDetail called for orderId: {}", request.getOrderId());
            
            Order order = orderUseCase.findById(request.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            
            OrderProto.Order orderProto = orderGrpcMapper.toProto(order);
            
            GetOrderDetailResponse response = GetOrderDetailResponse.newBuilder()
                    .setOrder(orderProto)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception ex) {
            log.error("Error in GetOrderDetail gRPC call", ex);
            responseObserver.onError(ex);
        }
    }
    
    @Override
    public void updateOrderStatus(UpdateOrderStatusRequest request, StreamObserver<UpdateOrderStatusResponse> responseObserver) {
        try {
            log.info("gRPC UpdateOrderStatus called for orderId: {}, status: {}", 
                    request.getOrderId(), request.getStatus());
            
            OrderStatus newStatus = OrderStatus.valueOf(request.getStatus().toUpperCase());
            Order updatedOrder = orderUseCase.updateStatus(request.getOrderId(), newStatus)
                    .orElseThrow(() -> new RuntimeException("Failed to update order status"));
            
            OrderProto.Order orderProto = orderGrpcMapper.toProto(updatedOrder);
            
            UpdateOrderStatusResponse response = UpdateOrderStatusResponse.newBuilder()
                    .setOrder(orderProto)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception ex) {
            log.error("Error in UpdateOrderStatus gRPC call", ex);
            responseObserver.onError(ex);
        }
    }
    
    @Override
    public void getOrderStatistics(GetOrderStatisticsRequest request, StreamObserver<GetOrderStatisticsResponse> responseObserver) {
        try {
            log.info("gRPC GetOrderStatistics called with period: {}", request.getPeriod());
            
            LocalDate dateFrom = request.getDateFrom().isEmpty() ? null : 
                    LocalDate.parse(request.getDateFrom(), DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate dateTo = request.getDateTo().isEmpty() ? null : 
                    LocalDate.parse(request.getDateTo(), DateTimeFormatter.ISO_LOCAL_DATE);
            
            // Get statistics from use case
            var statistics = orderUseCase.getOrderStatistics(request.getPeriod(), dateFrom, dateTo);
            
            GetOrderStatisticsResponse.Builder responseBuilder = GetOrderStatisticsResponse.newBuilder()
                    .setTotalOrders(statistics.getTotalOrders())
                    .setTotalRevenue(statistics.getTotalRevenue())
                    .setPendingOrders(statistics.getPendingOrders())
                    .setCompletedOrders(statistics.getCompletedOrders())
                    .setCancelledOrders(statistics.getCancelledOrders());
            
            // Add daily stats
            statistics.getDailyStats().forEach(dailyStat -> {
                DailyStats dailyStatsProto = DailyStats.newBuilder()
                        .setDate(dailyStat.getDate().toString())
                        .setOrders(dailyStat.getOrders())
                        .setRevenue(dailyStat.getRevenue())
                        .build();
                responseBuilder.addDailyStats(dailyStatsProto);
            });
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            
        } catch (Exception ex) {
            log.error("Error in GetOrderStatistics gRPC call", ex);
            responseObserver.onError(ex);
        }
    }
}