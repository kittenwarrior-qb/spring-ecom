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

            // Update stock
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());

            // Update cost_price (weighted average)
            BigDecimal oldTotal = product.getCostPrice().multiply(BigDecimal.valueOf(product.getStockQuantity() - item.getQuantity()));
            BigDecimal newTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal weightedAvg = oldTotal.add(newTotal)
                    .divide(BigDecimal.valueOf(product.getStockQuantity()), 2, java.math.RoundingMode.HALF_UP);
            product.setCostPrice(weightedAvg);

            productRepository.save(product);

            // Create inventory movement
            InventoryMovementEntity movement = InventoryMovementEntity.builder()
                    .productId(item.getProductId())
                    .movementType(MovementType.IMPORT)
                    .quantity(item.getQuantity())
                    .referenceType("PURCHASE_ORDER")
                    .referenceId(id)
                    .note("Import from PO: " + entity.getPoNumber())
                    .createdBy(receivedBy)
                    .build();
            inventoryMovementRepository.save(movement);
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

