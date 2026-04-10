package com.example.spring_ecom.repository.database.inventory;

import com.example.spring_ecom.domain.inventory.PurchaseOrderStatus;
import com.example.spring_ecom.repository.database.inventory.dao.PurchaseOrderWithSupplierDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderEntity, Long> {
    
    Optional<PurchaseOrderEntity> findByPoNumber(String poNumber);
    
    boolean existsByPoNumber(String poNumber);
    
    @Query("""
        SELECT new com.example.spring_ecom.repository.database.inventory.dao.PurchaseOrderWithSupplierDao(
               po.id, po.poNumber, po.supplierId, s.name,
               po.status, po.totalAmount, po.note,
               po.createdBy, po.createdAt, po.updatedAt)
        FROM PurchaseOrderEntity po
        JOIN SupplierEntity s ON po.supplierId = s.id
        WHERE po.deletedAt IS NULL
        AND (:status IS NULL OR po.status = :status)
        AND (:supplierId IS NULL OR po.supplierId = :supplierId)
        ORDER BY po.createdAt DESC
    """)
    Page<PurchaseOrderWithSupplierDao> findAllWithSupplier(
        @Param("status") PurchaseOrderStatus status,
        @Param("supplierId") Long supplierId,
        Pageable pageable
    );
    
    @Query("""
        SELECT po FROM PurchaseOrderEntity po
        WHERE po.deletedAt IS NULL
        AND (:status IS NULL OR po.status = :status)
        ORDER BY po.createdAt DESC
    """)
    Page<PurchaseOrderEntity> findWithFilters(
        @Param("status") PurchaseOrderStatus status,
        Pageable pageable
    );
    
    @Query("SELECT COUNT(po) FROM PurchaseOrderEntity po WHERE po.deletedAt IS NULL AND (po.status = 'DRAFT' OR po.status = 'CONFIRMED')")
    Long countPendingPurchaseOrders();
}

