package com.example.spring_ecom.controller.api.admin.supplier.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.domain.supplier.Supplier;
import org.mapstruct.Mapper;

@Mapper(config = MapStructGlobalConfig.class)
public interface SupplierResponseMapper {

    SupplierResponse toResponse(Supplier domain);
}

