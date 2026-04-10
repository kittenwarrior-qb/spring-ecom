package com.example.spring_ecom.repository.database.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductCostBatchRepository extends JpaRepository<ProductCostBatchEntity, Long> {

    /**
     * Find batches with remaining stock for a product, ordered oldest first (FIFO)
     */
    @Query("""
        SELECT b FROM ProductCostBatchEntity b
        WHERE b.productId = :productId AND b.quantityRemaining > 0
        ORDER BY b.receivedAt ASC
    """)
    List<ProductCostBatchEntity> findAvailableBatchesByProductId(@Param("productId") Long productId);

    /**
     * Get total inventory valuation (sum of quantityRemaining * costPrice across all batches)
     */
    @Query("""
        SELECT COALESCE(SUM(b.quantityRemaining * b.costPrice), 0)
        FROM ProductCostBatchEntity b
        WHERE b.quantityRemaining > 0
    """)
    BigDecimal getTotalInventoryValuation();

    /**
     * Get inventory valuation for a specific product
     */
    @Query("""
        SELECT COALESCE(SUM(b.quantityRemaining * b.costPrice), 0)
        FROM ProductCostBatchEntity b
        WHERE b.productId = :productId AND b.quantityRemaining > 0
    """)
    BigDecimal getProductInventoryValuation(@Param("productId") Long productId);

    /**
     * Count remaining stock from batches for a product
     */
    @Query("""
        SELECT COALESCE(SUM(b.quantityRemaining), 0)
        FROM ProductCostBatchEntity b
        WHERE b.productId = :productId AND b.quantityRemaining > 0
    """)
    Integer getTotalRemainingByProductId(@Param("productId") Long productId);
}

