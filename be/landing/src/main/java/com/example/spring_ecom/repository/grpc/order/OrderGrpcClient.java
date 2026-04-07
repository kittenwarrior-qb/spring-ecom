package com.example.spring_ecom.repository.grpc.order;

import com.example.spring_ecom.grpc.services.OrderServiceGrpc;
import com.example.spring_ecom.grpc.services.OrderServiceProto.*;
import com.example.spring_ecom.grpc.domain.OrderProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderGrpcClient {

    @GrpcClient("core-service")
    private OrderServiceGrpc.OrderServiceBlockingStub stub;

    public List<OrderProto.Order> getOrders(int page, int size, String search, String status,
                                           String paymentStatus, LocalDate dateFrom, LocalDate dateTo) {
        try {
            GetOrdersRequest.Builder requestBuilder = GetOrdersRequest.newBuilder()
                    .setPageRequest(
                            com.example.spring_ecom.grpc.common.CommonProto.PageRequest.newBuilder()
                                    .setPage(page)
                                    .setSize(size)
                                    .build()
                    );

            if (Objects.nonNull(search) && !search.isEmpty()) {
                requestBuilder.setSearch(search);
            }
            if (Objects.nonNull(status) && !status.isEmpty()) {
                requestBuilder.setStatus(status);
            }
            if (Objects.nonNull(paymentStatus) && !paymentStatus.isEmpty()) {
                requestBuilder.setPaymentStatus(paymentStatus);
            }
            if (Objects.nonNull(dateFrom)) {
                requestBuilder.setDateFrom(dateFrom.toString());
            }
            if (Objects.nonNull(dateTo)) {
                requestBuilder.setDateTo(dateTo.toString());
            }

            GetOrdersResponse response = stub.getOrders(requestBuilder.build());
            return response.getOrdersList();

        } catch (Exception e) {
            log.error("[GRPC] GetOrders failed: {}", e.getMessage());
            return List.of();
        }
    }

    public Optional<OrderProto.Order> getOrderDetail(Long orderId) {
        try {
            GetOrderDetailRequest request = GetOrderDetailRequest.newBuilder()
                    .setOrderId(orderId)
                    .build();

            GetOrderDetailResponse response = stub.getOrderDetail(request);
            return Optional.of(response.getOrder());

        } catch (Exception e) {
            log.error("[GRPC] GetOrderDetail failed: orderId={}, error={}", orderId, e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<OrderProto.Order> updateOrderStatus(Long orderId, String status) {
        try {
            UpdateOrderStatusRequest request = UpdateOrderStatusRequest.newBuilder()
                    .setOrderId(orderId)
                    .setStatus(status)
                    .build();

            UpdateOrderStatusResponse response = stub.updateOrderStatus(request);
            return Optional.of(response.getOrder());

        } catch (Exception e) {
            log.error("[GRPC] UpdateOrderStatus failed: orderId={}, status={}, error={}",
                    orderId, status, e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<GetOrderStatisticsResponse> getOrderStatistics(String period, LocalDate dateFrom, LocalDate dateTo) {
        try {
            GetOrderStatisticsRequest.Builder requestBuilder = GetOrderStatisticsRequest.newBuilder()
                    .setPeriod(period);

            if (Objects.nonNull(dateFrom)) {
                requestBuilder.setDateFrom(dateFrom.toString());
            }
            if (Objects.nonNull(dateTo)) {
                requestBuilder.setDateTo(dateTo.toString());
            }

            GetOrderStatisticsResponse response = stub.getOrderStatistics(requestBuilder.build());
            return Optional.of(response);

        } catch (Exception e) {
            log.error("[GRPC] GetOrderStatistics failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public boolean validateOrderExists(Long orderId) {
        try {
            Optional<OrderProto.Order> order = getOrderDetail(orderId);
            return order.isPresent();

        } catch (Exception e) {
            log.error("[GRPC] ValidateOrderExists failed: orderId={}, error={}", orderId, e.getMessage());
            return false;
        }
    }
}