package com.example.spring_ecom.repository.database.address;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.address.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructGlobalConfig.class)
public interface AddressEntityMapper extends BaseEntityMapper<Address, AddressEntity> {
    
    AddressEntity toEntity(Address source);
    
    void update(@MappingTarget AddressEntity target, Address source);
}
