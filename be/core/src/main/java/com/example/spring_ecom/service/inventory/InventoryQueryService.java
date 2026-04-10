package com.example.spring_ecom.service.inventory;

import com.example.spring_ecom.domain.inventory.InventoryMovement;
import com.example.spring_ecom.domain.inventory.MovementType;
import com.example.spring_ecom.domain.inventory.PurchaseOrder;
import com.example.spring_ecom.domain.inventory.PurchaseOrderItem;
import com.example.spring_ecom.domain.inventory.PurchaseOrderStatus;
import com.example.spring_ecom.repository.database.inventory.*;
import com.example.spring_ecom.repository.database.inventory.dao.PurchaseOrderWithSupplierDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryQueryService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final ProductCostBatchRepository batchRepository;
    private final InventoryEntityMapper mapper;

    // ========== Purchase Order Queries ==========

    public Page<PurchaseOrderWithSupplierDao> findAllPurchaseOrders(PurchaseOrderStatus status, Long supplierId, Pageable pageable) {
        return purchaseOrderRepository.findAllWithSupplier(status, supplierId, pageable);
    }

    public Optional<PurchaseOrder> findPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id)
                .filter(e -> Objects.isNull(e.getDeletedAt()))
                .map(mapper::toDomain);
    }

    public List<PurchaseOrderItem> findPurchaseOrderItems(Long purchaseOrderId) {
        return purchaseOrderItemRepository.findByPurchaseOrderId(purchaseOrderId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    // ========== Inventory Movement Queries ==========

    public Page<InventoryMovement> findMovements(Long productId, MovementType type, Pageable pageable) {
        return inventoryMovementRepository.findWithFilters(productId, type, pageable)
                .map(mapper::toDomain);
    }

    // ========== Inventory Valuation Queries ==========

    /**
     * Get total inventory valuation across all products based on cost batches.
     */
    public BigDecimal getTotalInventoryValuation() {
        return batchRepository.getTotalInventoryValuation();
    }

    /**
     * Get inventory valuation for a specific product.
     */
    public BigDecimal getProductInventoryValuation(Long productId) {
        return batchRepository.getProductInventoryValuation(productId);
    }
}
