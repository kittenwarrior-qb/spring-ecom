package com.example.spring_ecom.repository.database.purchaseOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItemEntity, Long> {

    List<PurchaseOrderItemEntity> findByPurchaseOrderId(Long purchaseOrderId);

    void deleteByPurchaseOrderId(Long purchaseOrderId);
}

