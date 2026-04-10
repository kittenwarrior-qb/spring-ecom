package com.example.spring_ecom.service.inventory;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.inventory.*;
import com.example.spring_ecom.repository.database.inventory.*;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import com.example.spring_ecom.repository.database.product.ProductRepository;
import com.example.spring_ecom.repository.database.supplier.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryCommandService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final ProductCostBatchRepository batchRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final InventoryEntityMapper mapper;

    private static final AtomicLong PO_COUNTER = new AtomicLong(0);

    // ========== Purchase Order CRUD ==========

    public PurchaseOrder createPurchaseOrder(PurchaseOrder po, List<PurchaseOrderItem> items) {
        validateSupplier(po.supplierId());

        String poNumber = generatePoNumber();

        PurchaseOrderEntity entity = mapper.toEntity(po);
        entity.setPoNumber(poNumber);
        entity.setStatus(PurchaseOrderStatus.DRAFT);

        // Calculate total
        BigDecimal total = calculateTotal(items);
        entity.setTotalAmount(total);

        purchaseOrderRepository.save(entity);

        // Save items
        saveItems(entity.getId(), items);

        log.info("[INVENTORY] Created PO: {} with {} items, total: {}", poNumber, items.size(), total);
        return mapper.toDomain(entity);
    }

    public PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrder po, List<PurchaseOrderItem> items) {
        PurchaseOrderEntity entity = findActivePO(id);

        if (entity.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Only DRAFT purchase orders can be updated");
        }

        validateSupplier(po.supplierId());

        entity.setSupplierId(po.supplierId());
        entity.setNote(po.note());

        // Recalculate total
        BigDecimal total = calculateTotal(items);
        entity.setTotalAmount(total);

        purchaseOrderRepository.save(entity);

        // Replace items
        purchaseOrderItemRepository.deleteByPurchaseOrderId(id);
        saveItems(id, items);

        log.info("[INVENTORY] Updated PO: {}", entity.getPoNumber());
        return mapper.toDomain(entity);
    }

    public PurchaseOrder confirmPurchaseOrder(Long id) {
        PurchaseOrderEntity entity = findActivePO(id);

        if (entity.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Only DRAFT purchase orders can be confirmed");
        }

        entity.setStatus(PurchaseOrderStatus.CONFIRMED);
        purchaseOrderRepository.save(entity);

        log.info("[INVENTORY] Confirmed PO: {}", entity.getPoNumber());
        return mapper.toDomain(entity);
    }

    public PurchaseOrder receivePurchaseOrder(Long id, Long receivedBy) {
        PurchaseOrderEntity entity = findActivePO(id);

        if (entity.getStatus() != PurchaseOrderStatus.CONFIRMED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Only CONFIRMED purchase orders can be received");
        }

        List<PurchaseOrderItemEntity> items = purchaseOrderItemRepository.findByPurchaseOrderId(id);

        // Update product stock + cost_price + create inventory movements
        for (PurchaseOrderItemEntity item : items) {
            ProductEntity product = productRepository.findByIdWithLock(item.getProductId())
                    .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND,
                            "Product not found: " + item.getProductId()));

            int stockBefore = product.getStockQuantity();

            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());

            // Update cost_price (weighted average)
            BigDecimal oldTotal = product.getCostPrice().multiply(BigDecimal.valueOf(stockBefore));
            BigDecimal newTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal weightedAvg = oldTotal.add(newTotal)
                    .divide(BigDecimal.valueOf(product.getStockQuantity()), 2, RoundingMode.HALF_UP);
            product.setCostPrice(weightedAvg);

            productRepository.save(product);

            // Record inventory transaction
            recordPurchaseIn(item.getProductId(), item.getQuantity(), item.getUnitPrice(),
                    stockBefore, product.getStockQuantity(), id, entity.getPoNumber(), receivedBy);

            // Create cost batch for FIFO tracking
            createCostBatch(item.getProductId(), item.getId(), item.getQuantity(), item.getUnitPrice());
        }

        entity.setStatus(PurchaseOrderStatus.RECEIVED);
        purchaseOrderRepository.save(entity);

        log.info("[INVENTORY] Received PO: {}, updated {} products", entity.getPoNumber(), items.size());
        return mapper.toDomain(entity);
    }

    public void cancelPurchaseOrder(Long id) {
        PurchaseOrderEntity entity = findActivePO(id);

        if (entity.getStatus() == PurchaseOrderStatus.RECEIVED) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Cannot cancel a received purchase order");
        }

        entity.setStatus(PurchaseOrderStatus.CANCELLED);
        entity.setDeletedAt(LocalDateTime.now());
        purchaseOrderRepository.save(entity);

        log.info("[INVENTORY] Cancelled PO: {}", entity.getPoNumber());
    }

    // ========== Inventory Movement Recording ==========

    /**
     * Record an inventory movement with full audit trail
     */
    public void recordMovement(Long productId, MovementType type, int quantity,
                               BigDecimal costPrice, int stockBefore, int stockAfter,
                               String referenceType, Long referenceId,
                               String note, Long createdBy) {
        InventoryMovementEntity movement = InventoryMovementEntity.builder()
                .productId(productId)
                .movementType(type)
                .quantity(quantity)
                .costPrice(costPrice)
                .stockBefore(stockBefore)
                .stockAfter(stockAfter)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .note(note)
                .createdBy(createdBy)
                .build();
        inventoryMovementRepository.save(movement);
        log.info("[INVENTORY_TX] {} productId={} qty={} stockBefore={} stockAfter={} ref={}:{}",
                type, productId, quantity, stockBefore, stockAfter, referenceType, referenceId);
    }

    public void recordPurchaseIn(Long productId, int quantity, BigDecimal costPrice,
                                 int stockBefore, int stockAfter, Long poId, String poNumber, Long receivedBy) {
        recordMovement(productId, MovementType.IMPORT, quantity, costPrice,
                stockBefore, stockAfter, "PURCHASE_ORDER", poId,
                "Import from PO: " + poNumber, receivedBy);
    }

    public void recordSaleOut(Long productId, int quantity, BigDecimal costPrice,
                              int stockBefore, int stockAfter, Long orderId, String orderNumber) {
        recordMovement(productId, MovementType.SALE_OUT, -quantity, costPrice,
                stockBefore, stockAfter, "ORDER", orderId,
                "Sale from order: " + orderNumber, null);
    }

    public void recordReturnIn(Long productId, int quantity,
                               int stockBefore, int stockAfter, Long orderId, String orderNumber) {
        recordMovement(productId, MovementType.RETURN, quantity, null,
                stockBefore, stockAfter, "ORDER", orderId,
                "Return from cancelled order: " + orderNumber, null);
    }

    public void recordAdjustment(Long productId, int quantityDelta,
                                 int stockBefore, int stockAfter, String note, Long adjustedBy) {
        recordMovement(productId, MovementType.ADJUSTMENT, quantityDelta, null,
                stockBefore, stockAfter, "MANUAL", null,
                note != null ? note : "Manual stock adjustment", adjustedBy);
    }

    // ========== Cost Batch Tracking ==========

    /**
     * Create a cost batch when goods are received from a purchase order.
     */
    public void createCostBatch(Long productId, Long purchaseOrderItemId,
                                int quantity, BigDecimal costPrice) {
        ProductCostBatchEntity batch = ProductCostBatchEntity.builder()
                .productId(productId)
                .purchaseOrderItemId(purchaseOrderItemId)
                .quantityRemaining(quantity)
                .costPrice(costPrice)
                .receivedAt(LocalDateTime.now())
                .build();
        batchRepository.save(batch);
        log.info("[COST_BATCH] Created batch: productId={}, qty={}, costPrice={}, poItemId={}",
                productId, quantity, costPrice, purchaseOrderItemId);
    }

    /**
     * Consume stock from oldest batches first (FIFO) and return the weighted average
     * cost price for the consumed quantity. Used to set cost_price on order_items.
     */
    public BigDecimal consumeBatchesFIFO(Long productId, int quantity) {
        List<ProductCostBatchEntity> batches = batchRepository.findAvailableBatchesByProductId(productId);

        if (batches.isEmpty()) {
            log.warn("[COST_BATCH] No available batches for productId={}, returning ZERO cost", productId);
            return BigDecimal.ZERO;
        }

        int remaining = quantity;
        BigDecimal totalCost = BigDecimal.ZERO;
        int totalConsumed = 0;

        for (ProductCostBatchEntity batch : batches) {
            if (remaining <= 0) break;

            int consumeFromBatch = Math.min(remaining, batch.getQuantityRemaining());
            totalCost = totalCost.add(batch.getCostPrice().multiply(BigDecimal.valueOf(consumeFromBatch)));
            totalConsumed += consumeFromBatch;

            batch.setQuantityRemaining(batch.getQuantityRemaining() - consumeFromBatch);
            batchRepository.save(batch);

            remaining -= consumeFromBatch;

            log.debug("[COST_BATCH] Consumed {} from batchId={} (remaining={}), costPrice={}",
                    consumeFromBatch, batch.getId(), batch.getQuantityRemaining(), batch.getCostPrice());
        }

        if (remaining > 0) {
            log.warn("[COST_BATCH] Not enough batches for productId={}, shortfall={}", productId, remaining);
        }

        if (totalConsumed == 0) return BigDecimal.ZERO;
        return totalCost.divide(BigDecimal.valueOf(totalConsumed), 2, RoundingMode.HALF_UP);
    }

    // ========== Support Methods ==========

    private PurchaseOrderEntity findActivePO(Long id) {
        return purchaseOrderRepository.findById(id)
                .filter(e -> Objects.isNull(e.getDeletedAt()))
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Purchase order not found"));
    }

    private void validateSupplier(Long supplierId) {
        supplierRepository.findByIdAndDeletedAtIsNull(supplierId)
                .orElseThrow(() -> new BaseException(ResponseCode.BAD_REQUEST, "Supplier not found"));
    }

    private BigDecimal calculateTotal(List<PurchaseOrderItem> items) {
        return items.stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void saveItems(Long purchaseOrderId, List<PurchaseOrderItem> items) {
        for (PurchaseOrderItem item : items) {
            PurchaseOrderItemEntity itemEntity = mapper.toEntity(item);
            itemEntity.setPurchaseOrderId(purchaseOrderId);
            itemEntity.setTotalPrice(item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())));
            purchaseOrderItemRepository.save(itemEntity);
        }
    }

    private String generatePoNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long counter = PO_COUNTER.incrementAndGet();
        String poNumber = "PO-" + datePart + "-" + String.format("%04d", counter % 10000);

        // Ensure uniqueness
        while (purchaseOrderRepository.existsByPoNumber(poNumber)) {
            counter = PO_COUNTER.incrementAndGet();
            poNumber = "PO-" + datePart + "-" + String.format("%04d", counter % 10000);
        }

        return poNumber;
    }
}
