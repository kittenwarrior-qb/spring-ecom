package com.example.spring_ecom.controller.api.user.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseModelMapper;
import com.example.spring_ecom.domain.user.User;
import org.mapstruct.Mapper;

@Mapper(config = MapStructGlobalConfig.class)
public interface UserResponseMapper extends BaseModelMapper<UserResponse, User> {
}
