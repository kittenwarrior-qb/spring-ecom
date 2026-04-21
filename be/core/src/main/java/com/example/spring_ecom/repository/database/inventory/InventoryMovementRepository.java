package com.example.spring_ecom.repository.database.inventory;

import com.example.spring_ecom.domain.inventory.MovementType;
import com.example.spring_ecom.repository.database.inventory.dao.InventoryMovementWithProductDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovementEntity, Long> {

    @Query("""
        SELECT m FROM InventoryMovementEntity m
        WHERE (:productId IS NULL OR m.productId = :productId)
        AND (:movementType IS NULL OR m.movementType = :movementType)
        ORDER BY m.createdAt DESC
    """)
    Page<InventoryMovementEntity> findWithFilters(
        @Param("productId") Long productId,
        @Param("movementType") MovementType movementType,
        Pageable pageable
    );

    @Query("""
        SELECT new com.example.spring_ecom.repository.database.inventory.dao.InventoryMovementWithProductDao(
            m.id, m.productId, p.title, p.coverImageUrl, m.movementType, m.quantity, m.costPrice,
            m.stockBefore, m.stockAfter, m.referenceType, m.referenceId,
            m.note, m.createdBy, m.createdAt
        )
        FROM InventoryMovementEntity m
        LEFT JOIN ProductEntity p ON m.productId = p.id
        WHERE (:productId IS NULL OR m.productId = :productId)
        AND (:movementType IS NULL OR m.movementType = :movementType)
        ORDER BY m.createdAt DESC
    """)
    Page<InventoryMovementWithProductDao> findWithProduct(
        @Param("productId") Long productId,
        @Param("movementType") MovementType movementType,
        Pageable pageable
    );

    List<InventoryMovementEntity> findByReferenceTypeAndReferenceId(String referenceType, Long referenceId);
}

