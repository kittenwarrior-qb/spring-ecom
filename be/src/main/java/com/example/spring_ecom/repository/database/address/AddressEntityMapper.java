package com.example.spring_ecom.repository.database.address;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.address.Address;
import org.mapstruct.Mapper;

@Mapper(config = MapStructGlobalConfig.class)
public interface AddressEntityMapper extends BaseEntityMapper<Address, AddressEntity> {
    // Các method từ BaseEntityMapper đã có sẵn, không cần khai báo lại
}
