package com.example.spring_ecom.repository.database.supplier;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.supplier.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructGlobalConfig.class)
public interface SupplierEntityMapper extends BaseEntityMapper<Supplier, SupplierEntity> {

    @Override
    @Mapping(target = "isActive", defaultValue = "true")
    SupplierEntity toEntity(Supplier domain);

    @Override
    Supplier toDomain(SupplierEntity entity);

    @Mapping(target = "deletedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "contactName", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "note", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void markAsDeleted(@MappingTarget SupplierEntity entity, Supplier ignored);
}

