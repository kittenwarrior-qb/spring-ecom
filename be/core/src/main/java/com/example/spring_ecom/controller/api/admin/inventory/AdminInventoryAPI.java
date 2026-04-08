package com.example.spring_ecom.controller.api.admin.inventory;

import com.example.spring_ecom.controller.api.admin.inventory.model.CreatePurchaseOrderRequest;
import com.example.spring_ecom.controller.api.admin.inventory.model.InventoryMovementResponse;
import com.example.spring_ecom.controller.api.admin.inventory.model.PurchaseOrderResponse;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.domain.inventory.MovementType;
import com.example.spring_ecom.domain.inventory.PurchaseOrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/api/admin/inventory")
@Tag(name = "Admin Inventory Management", description = "Admin APIs for purchase orders & inventory movements")
public interface AdminInventoryAPI {

    // ========== Purchase Orders ==========

    @Operation(summary = "Get all purchase orders")
    @GetMapping("/purchase-orders")
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    ResponseEntity<ApiResponse<Page<PurchaseOrderResponse>>> getAllPurchaseOrders(
            Pageable pageable,
            @Parameter(description = "Filter by status") @RequestParam(required = false) PurchaseOrderStatus status,
            @Parameter(description = "Filter by supplier") @RequestParam(required = false) Long supplierId);

    @Operation(summary = "Get purchase order by ID with items")
    @GetMapping("/purchase-orders/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    ResponseEntity<ApiResponse<PurchaseOrderResponse>> getPurchaseOrderById(
            @Parameter(description = "Purchase Order ID") @PathVariable Long id);

    @Operation(summary = "Create purchase order")
    @PostMapping("/purchase-orders")
    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    ResponseEntity<ApiResponse<PurchaseOrderResponse>> createPurchaseOrder(
            @Valid @RequestBody CreatePurchaseOrderRequest request);

    @Operation(summary = "Update purchase order (DRAFT only)")
    @PutMapping("/purchase-orders/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    ResponseEntity<ApiResponse<PurchaseOrderResponse>> updatePurchaseOrder(
            @Parameter(description = "Purchase Order ID") @PathVariable Long id,
            @Valid @RequestBody CreatePurchaseOrderRequest request);

    @Operation(summary = "Confirm purchase order", description = "Change status from DRAFT to CONFIRMED")
    @PostMapping("/purchase-orders/{id}/confirm")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    ResponseEntity<ApiResponse<PurchaseOrderResponse>> confirmPurchaseOrder(
            @Parameter(description = "Purchase Order ID") @PathVariable Long id);

    @Operation(summary = "Receive purchase order", description = "Change status to RECEIVED and update product stock")
    @PostMapping("/purchase-orders/{id}/receive")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    ResponseEntity<ApiResponse<PurchaseOrderResponse>> receivePurchaseOrder(
            @Parameter(description = "Purchase Order ID") @PathVariable Long id);

    @Operation(summary = "Cancel purchase order")
    @PostMapping("/purchase-orders/{id}/cancel")
    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    ResponseEntity<ApiResponse<Void>> cancelPurchaseOrder(
            @Parameter(description = "Purchase Order ID") @PathVariable Long id);

    // ========== Inventory Movements ==========

    @Operation(summary = "Get inventory movements", description = "View inventory movement history")
    @GetMapping("/movements")
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    ResponseEntity<ApiResponse<Page<InventoryMovementResponse>>> getMovements(
            Pageable pageable,
            @Parameter(description = "Filter by product") @RequestParam(required = false) Long productId,
            @Parameter(description = "Filter by movement type") @RequestParam(required = false) MovementType movementType);
}

