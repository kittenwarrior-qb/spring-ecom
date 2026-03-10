package com.example.spring_ecom.controller.api.address.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseModelMapper;
import com.example.spring_ecom.domain.address.Address;
import org.mapstruct.Mapper;

@Mapper(config = MapStructGlobalConfig.class)
public interface AddressResponseMapper extends BaseModelMapper<AddressResponse, Address> {
}
