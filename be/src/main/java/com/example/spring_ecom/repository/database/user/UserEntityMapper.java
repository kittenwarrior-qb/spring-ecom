package com.example.spring_ecom.repository.database.user;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.user.User;
import org.mapstruct.Mapper;

@Mapper(config = MapStructGlobalConfig.class)
public interface UserEntityMapper extends BaseEntityMapper<User, UserEntity> {
}
