package com.example.spring_ecom.service.inventory;

import com.example.spring_ecom.domain.inventory.InventoryMovement;
import com.example.spring_ecom.domain.inventory.MovementType;
import com.example.spring_ecom.domain.inventory.PurchaseOrder;
import com.example.spring_ecom.domain.inventory.PurchaseOrderItem;
import com.example.spring_ecom.domain.inventory.PurchaseOrderStatus;
import com.example.spring_ecom.repository.database.inventory.dao.PurchaseOrderWithSupplierDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface InventoryUseCase {

    Page<PurchaseOrderWithSupplierDao> findAllPurchaseOrders(PurchaseOrderStatus status, Long supplierId, Pageable pageable);

    Optional<PurchaseOrder> findPurchaseOrderById(Long id);

    List<PurchaseOrderItem> findPurchaseOrderItems(Long purchaseOrderId);

    PurchaseOrder createPurchaseOrder(PurchaseOrder po, List<PurchaseOrderItem> items);

    PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrder po, List<PurchaseOrderItem> items);

    PurchaseOrder confirmPurchaseOrder(Long id);

    PurchaseOrder receivePurchaseOrder(Long id, Long receivedBy);

    void cancelPurchaseOrder(Long id);

    // ========== Inventory Movements ==========

    Page<InventoryMovement> findMovements(Long productId, MovementType type, Pageable pageable);
}

