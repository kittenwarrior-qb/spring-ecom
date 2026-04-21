package com.example.spring_ecom.controller.api.admin.dashboard;

import com.example.spring_ecom.controller.api.admin.dashboard.model.DashboardResponse;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.order.OrderStatistics;
import com.example.spring_ecom.repository.database.purchaseOrder.PurchaseOrderRepository;
import com.example.spring_ecom.repository.database.product.ProductRepository;
import com.example.spring_ecom.repository.database.supplier.SupplierRepository;
import com.example.spring_ecom.service.inventory.InventoryUseCase;
import com.example.spring_ecom.service.order.OrderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardController implements AdminDashboardAPI {

    private final OrderUseCase orderUseCase;
    private final InventoryUseCase inventoryUseCase;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Override
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(String period, String startDate, String endDate) {
        try {
            log.info("Admin getting dashboard: period={}", period);
            LocalDate from = Objects.nonNull(startDate) ? LocalDate.parse(startDate) : null;
            LocalDate to = Objects.nonNull(endDate) ? LocalDate.parse(endDate) : null;

            OrderStatistics stats = orderUseCase.getOrderStatistics(period, from, to);

            Long totalProducts = productRepository.countActiveProducts();
            Long lowStockProducts = productRepository.countLowStockProducts();
            Long outOfStockProducts = productRepository.countOutOfStockProducts();
            Long totalSuppliers = supplierRepository.countActiveSuppliers();
            Long pendingPOs = purchaseOrderRepository.countPendingPurchaseOrders();
            BigDecimal inventoryValuation = inventoryUseCase.getTotalInventoryValuation();

            DashboardResponse response = new DashboardResponse(
                    stats.getTotalOrders(),
                    stats.getTotalRevenue(),
                    stats.getTotalCost(),
                    stats.getTotalProfit(),
                    stats.getProfitMargin(),
                    stats.getTodayRevenue(),
                    stats.getPendingOrders(),
                    stats.getConfirmedOrders(),
                    stats.getShippedOrders(),
                    stats.getCompletedOrders(),
                    stats.getCancelledOrders(),
                    totalProducts,
                    lowStockProducts,
                    outOfStockProducts,
                    totalSuppliers,
                    pendingPOs,
                    inventoryValuation != null ? inventoryValuation : BigDecimal.ZERO,
                    stats.getDailyStats(),
                    stats.getTopProducts()
            );

            return ResponseEntity.ok(ApiResponse.Success.of(response));
        } catch (Exception e) {
            log.error("Error getting dashboard: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to get dashboard"));
        }
    }
}

