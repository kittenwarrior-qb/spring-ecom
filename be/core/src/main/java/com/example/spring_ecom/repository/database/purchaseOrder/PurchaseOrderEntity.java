package com.example.spring_ecom.repository.database.purchaseOrder;

import com.example.spring_ecom.domain.inventory.PurchaseOrderStatus;
import com.example.spring_ecom.repository.database.common.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderEntity extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "po_number", nullable = false, unique = true, length = 50)
    private String poNumber;
    
    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private PurchaseOrderStatus status = PurchaseOrderStatus.DRAFT;
    
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    
    @Column(name = "created_by")
    private Long createdBy;
}

