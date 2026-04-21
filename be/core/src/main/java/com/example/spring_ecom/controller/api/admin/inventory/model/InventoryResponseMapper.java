package com.example.spring_ecom.controller.api.admin.inventory.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.domain.inventory.InventoryMovement;
import com.example.spring_ecom.domain.inventory.PurchaseOrder;
import com.example.spring_ecom.domain.inventory.PurchaseOrderItem;
import com.example.spring_ecom.repository.database.inventory.dao.InventoryMovementWithProductDao;
import com.example.spring_ecom.repository.database.inventory.dao.PurchaseOrderWithSupplierDao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface InventoryResponseMapper {

    @Mapping(target = "supplierName", ignore = true)
    @Mapping(target = "items", ignore = true)
    PurchaseOrderResponse toResponse(PurchaseOrder domain);

    @Mapping(target = "items", ignore = true)
    PurchaseOrderResponse toResponse(PurchaseOrderWithSupplierDao dao);

    @Mapping(target = "productTitle", ignore = true)
    PurchaseOrderItemResponse toResponse(PurchaseOrderItem domain);

    InventoryMovementResponse toResponse(InventoryMovement domain);

    InventoryMovementResponse toResponse(InventoryMovementWithProductDao dao);
}

