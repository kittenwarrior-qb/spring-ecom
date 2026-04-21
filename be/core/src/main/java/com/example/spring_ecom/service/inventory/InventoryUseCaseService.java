package com.example.spring_ecom.service.inventory;

import com.example.spring_ecom.domain.inventory.InventoryMovement;
import com.example.spring_ecom.domain.inventory.MovementType;
import com.example.spring_ecom.domain.inventory.PurchaseOrder;
import com.example.spring_ecom.domain.inventory.PurchaseOrderItem;
import com.example.spring_ecom.domain.inventory.PurchaseOrderStatus;
import com.example.spring_ecom.repository.database.inventory.dao.InventoryMovementWithProductDao;
import com.example.spring_ecom.repository.database.inventory.dao.PurchaseOrderWithSupplierDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryUseCaseService implements InventoryUseCase {

    private final InventoryQueryService queryService;
    private final InventoryCommandService commandService;

    // ========== Purchase Order Queries ==========

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseOrderWithSupplierDao> findAllPurchaseOrders(PurchaseOrderStatus status, Long supplierId, Pageable pageable) {
        return queryService.findAllPurchaseOrders(status, supplierId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PurchaseOrder> findPurchaseOrderById(Long id) {
        return queryService.findPurchaseOrderById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderItem> findPurchaseOrderItems(Long purchaseOrderId) {
        return queryService.findPurchaseOrderItems(purchaseOrderId);
    }

    // ========== Purchase Order Commands ==========

    @Override
    @Transactional
    public PurchaseOrder createPurchaseOrder(PurchaseOrder po, List<PurchaseOrderItem> items) {
        return commandService.createPurchaseOrder(po, items);
    }

    @Override
    @Transactional
    public PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrder po, List<PurchaseOrderItem> items) {
        return commandService.updatePurchaseOrder(id, po, items);
    }

    @Override
    @Transactional
    public PurchaseOrder confirmPurchaseOrder(Long id) {
        return commandService.confirmPurchaseOrder(id);
    }

    @Override
    @Transactional
    public PurchaseOrder receivePurchaseOrder(Long id, Long receivedBy) {
        return commandService.receivePurchaseOrder(id, receivedBy);
    }

    @Override
    @Transactional
    public void cancelPurchaseOrder(Long id) {
        commandService.cancelPurchaseOrder(id);
    }

    // ========== Inventory Movement Queries ==========

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryMovement> findMovements(Long productId, MovementType type, Pageable pageable) {
        return queryService.findMovements(productId, type, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryMovementWithProductDao> findMovementsWithProduct(Long productId, MovementType type, Pageable pageable) {
        return queryService.findMovementsWithProduct(productId, type, pageable);
    }

    // ========== Inventory Movement Recording ==========

    @Override
    @Transactional
    public void recordSaleOut(Long productId, int quantity, BigDecimal costPrice,
                              int stockBefore, int stockAfter, Long orderId, String orderNumber) {
        commandService.recordSaleOut(productId, quantity, costPrice, stockBefore, stockAfter, orderId, orderNumber);
    }

    @Override
    @Transactional
    public void recordReturnIn(Long productId, int quantity,
                               int stockBefore, int stockAfter, Long orderId, String orderNumber) {
        commandService.recordReturnIn(productId, quantity, stockBefore, stockAfter, orderId, orderNumber);
    }

    @Override
    @Transactional
    public void recordAdjustment(Long productId, int quantityDelta,
                                 int stockBefore, int stockAfter, String note, Long adjustedBy) {
        commandService.recordAdjustment(productId, quantityDelta, stockBefore, stockAfter, note, adjustedBy);
    }

    // ========== Cost Batch Tracking ==========

    @Override
    @Transactional
    public BigDecimal consumeBatchesFIFO(Long productId, int quantity) {
        return commandService.consumeBatchesFIFO(productId, quantity);
    }

    // ========== Inventory Valuation ==========

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalInventoryValuation() {
        return queryService.getTotalInventoryValuation();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getProductInventoryValuation(Long productId) {
        return queryService.getProductInventoryValuation(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countPendingPurchaseOrders() {
        return queryService.countPendingPurchaseOrders();
    }
}
