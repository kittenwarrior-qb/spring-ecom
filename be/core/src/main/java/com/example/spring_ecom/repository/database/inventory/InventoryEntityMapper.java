package com.example.spring_ecom.repository.database.inventory;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.inventory.PurchaseOrder;
import com.example.spring_ecom.domain.inventory.PurchaseOrderItem;
import com.example.spring_ecom.domain.inventory.InventoryMovement;
import com.example.spring_ecom.repository.database.purchaseOrder.PurchaseOrderEntity;
import com.example.spring_ecom.repository.database.purchaseOrder.PurchaseOrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface InventoryEntityMapper extends BaseEntityMapper<PurchaseOrder, PurchaseOrderEntity> {

    @Override
    @Mapping(target = "status", defaultExpression = "java(com.example.spring_ecom.domain.inventory.PurchaseOrderStatus.DRAFT)")
    @Mapping(target = "totalAmount", defaultExpression = "java(java.math.BigDecimal.ZERO)")
    PurchaseOrderEntity toEntity(PurchaseOrder domain);

    @Override
    PurchaseOrder toDomain(PurchaseOrderEntity entity);

    PurchaseOrderItemEntity toEntity(PurchaseOrderItem domain);

    PurchaseOrderItem toDomain(PurchaseOrderItemEntity entity);

    InventoryMovementEntity toEntity(InventoryMovement domain);

    InventoryMovement toDomain(InventoryMovementEntity entity);
}

