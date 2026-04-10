package com.example.spring_ecom.service.inventory;

import com.example.spring_ecom.domain.inventory.InventoryMovement;
import com.example.spring_ecom.domain.inventory.MovementType;
import com.example.spring_ecom.domain.inventory.PurchaseOrder;
import com.example.spring_ecom.domain.inventory.PurchaseOrderItem;
import com.example.spring_ecom.domain.inventory.PurchaseOrderStatus;
import com.example.spring_ecom.repository.database.inventory.dao.PurchaseOrderWithSupplierDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InventoryUseCase {

    // ========== Queries ==========

    Page<PurchaseOrderWithSupplierDao> findAllPurchaseOrders(PurchaseOrderStatus status, Long supplierId, Pageable pageable);

    Optional<PurchaseOrder> findPurchaseOrderById(Long id);

    List<PurchaseOrderItem> findPurchaseOrderItems(Long purchaseOrderId);

    // ========== Commands ==========

    PurchaseOrder createPurchaseOrder(PurchaseOrder po, List<PurchaseOrderItem> items);

    PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrder po, List<PurchaseOrderItem> items);

    PurchaseOrder confirmPurchaseOrder(Long id);

    PurchaseOrder receivePurchaseOrder(Long id, Long receivedBy);

    void cancelPurchaseOrder(Long id);

    // ========== Inventory Movement Queries ==========

    Page<InventoryMovement> findMovements(Long productId, MovementType type, Pageable pageable);

    void recordSaleOut(Long productId, int quantity, BigDecimal costPrice,
                       int stockBefore, int stockAfter, Long orderId, String orderNumber);

    void recordReturnIn(Long productId, int quantity,
                        int stockBefore, int stockAfter, Long orderId, String orderNumber);

    void recordAdjustment(Long productId, int quantityDelta,
                          int stockBefore, int stockAfter, String note, Long adjustedBy);

    BigDecimal consumeBatchesFIFO(Long productId, int quantity);

    // ========== Inventory Valuation ==========

    BigDecimal getTotalInventoryValuation();

    BigDecimal getProductInventoryValuation(Long productId);
}
