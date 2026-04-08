package com.example.spring_ecom.service.inventory;

import com.example.spring_ecom.domain.inventory.InventoryMovement;
import com.example.spring_ecom.domain.inventory.MovementType;
import com.example.spring_ecom.domain.inventory.PurchaseOrder;
import com.example.spring_ecom.domain.inventory.PurchaseOrderItem;
import com.example.spring_ecom.domain.inventory.PurchaseOrderStatus;
import com.example.spring_ecom.repository.database.inventory.dao.PurchaseOrderWithSupplierDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryUseCaseService implements InventoryUseCase {

    private final InventoryQueryService queryService;
    private final InventoryCommandService commandService;

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

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryMovement> findMovements(Long productId, MovementType type, Pageable pageable) {
        return queryService.findMovements(productId, type, pageable);
    }
}

