package com.example.spring_ecom.repository.database.user;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.domain.user.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface UserEntityMapper extends BaseEntityMapper<User, UserEntity> {
    
    @Override
    @Mapping(target = "isActive", defaultValue = "true")
    @Mapping(target = "isEmailVerified", defaultValue = "false")
    @Mapping(target = "role", defaultValue = "USER")
    UserEntity toEntity(User domain);
}
