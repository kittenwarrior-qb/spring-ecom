package com.example.spring_ecom.controller.api.admin.inventory;

import com.example.spring_ecom.controller.api.admin.inventory.model.*;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.SecurityUtil;
import com.example.spring_ecom.domain.inventory.*;
import com.example.spring_ecom.repository.database.inventory.dao.PurchaseOrderWithSupplierDao;
import com.example.spring_ecom.service.inventory.InventoryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminInventoryController implements AdminInventoryAPI {

    private final InventoryUseCase inventoryUseCase;
    private final InventoryResponseMapper responseMapper;

    // ========== Purchase Orders ==========

    @Override
    public ResponseEntity<ApiResponse<Page<PurchaseOrderResponse>>> getAllPurchaseOrders(
            Pageable pageable, PurchaseOrderStatus status, Long supplierId) {
        log.info("Admin getting purchase orders, status={}, supplierId={}", status, supplierId);
        Page<PurchaseOrderResponse> response = inventoryUseCase
                .findAllPurchaseOrders(status, supplierId, pageable)
                .map(dao -> responseMapper.toResponse(dao));
        return ResponseEntity.ok(ApiResponse.Success.of(response));
    }

    @Override
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> getPurchaseOrderById(Long id) {
        log.info("Admin getting purchase order by ID: {}", id);
        return inventoryUseCase.findPurchaseOrderById(id)
                .map(po -> {
                    PurchaseOrderResponse resp = responseMapper.toResponse(po);
                    List<PurchaseOrderItem> items = inventoryUseCase.findPurchaseOrderItems(id);
                    List<PurchaseOrderItemResponse> itemResponses = items.stream()
                            .map(responseMapper::toResponse)
                            .toList();
                    // Rebuild response with items
                    resp = new PurchaseOrderResponse(
                            resp.id(), resp.poNumber(), resp.supplierId(), resp.supplierName(),
                            resp.status(), resp.totalAmount(), resp.note(),
                            resp.createdBy(), resp.createdAt(), resp.updatedAt(), itemResponses
                    );
                    return ResponseEntity.ok(ApiResponse.Success.of(resp));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> createPurchaseOrder(CreatePurchaseOrderRequest request) {
        log.info("Admin creating purchase order for supplier: {}", request.getSupplierId());
        Long currentUserId = SecurityUtil.getCurrentUserId();

        PurchaseOrder po = new PurchaseOrder(
                null, null, request.getSupplierId(),
                PurchaseOrderStatus.DRAFT, BigDecimal.ZERO,
                request.getNote(), currentUserId,
                null, null, null
        );

        List<PurchaseOrderItem> items = request.getItems().stream()
                .map(item -> new PurchaseOrderItem(
                        null, null, item.getProductId(),
                        item.getQuantity(), item.getUnitPrice(),
                        item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())),
                        null, null
                ))
                .toList();

        PurchaseOrder created = inventoryUseCase.createPurchaseOrder(po, items);
        PurchaseOrderResponse response = responseMapper.toResponse(created);
        return ResponseEntity.ok(
                ApiResponse.Success.of(ResponseCode.CREATED, "Purchase order created successfully", response));
    }

    @Override
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> updatePurchaseOrder(
            Long id, CreatePurchaseOrderRequest request) {
        log.info("Admin updating purchase order: {}", id);

        PurchaseOrder po = new PurchaseOrder(
                id, null, request.getSupplierId(),
                null, BigDecimal.ZERO,
                request.getNote(), null,
                null, null, null
        );

        List<PurchaseOrderItem> items = request.getItems().stream()
                .map(item -> new PurchaseOrderItem(
                        null, id, item.getProductId(),
                        item.getQuantity(), item.getUnitPrice(),
                        item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())),
                        null, null
                ))
                .toList();

        PurchaseOrder updated = inventoryUseCase.updatePurchaseOrder(id, po, items);
        PurchaseOrderResponse response = responseMapper.toResponse(updated);
        return ResponseEntity.ok(
                ApiResponse.Success.of(ResponseCode.OK, "Purchase order updated successfully", response));
    }

    @Override
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> confirmPurchaseOrder(Long id) {
        log.info("Admin confirming purchase order: {}", id);
        PurchaseOrder confirmed = inventoryUseCase.confirmPurchaseOrder(id);
        return ResponseEntity.ok(
                ApiResponse.Success.of(ResponseCode.OK, "Purchase order confirmed", responseMapper.toResponse(confirmed)));
    }

    @Override
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> receivePurchaseOrder(Long id) {
        log.info("Admin receiving purchase order: {}", id);
        Long currentUserId = SecurityUtil.getCurrentUserId();
        PurchaseOrder received = inventoryUseCase.receivePurchaseOrder(id, currentUserId);
        return ResponseEntity.ok(
                ApiResponse.Success.of(ResponseCode.OK, "Purchase order received, stock updated", responseMapper.toResponse(received)));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> cancelPurchaseOrder(Long id) {
        log.info("Admin cancelling purchase order: {}", id);
        inventoryUseCase.cancelPurchaseOrder(id);
        return ResponseEntity.ok(
                ApiResponse.Success.of(ResponseCode.OK, "Purchase order cancelled", null));
    }

    // ========== Inventory Movements ==========

    @Override
    public ResponseEntity<ApiResponse<Page<InventoryMovementResponse>>> getMovements(
            Pageable pageable, Long productId, MovementType movementType) {
        log.info("Admin getting inventory movements, productId={}, type={}", productId, movementType);
        Page<InventoryMovementResponse> response = inventoryUseCase.findMovements(productId, movementType, pageable)
                .map(responseMapper::toResponse);
        return ResponseEntity.ok(ApiResponse.Success.of(response));
    }
}

