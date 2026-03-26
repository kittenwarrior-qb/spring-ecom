package com.example.spring_ecom.repository.database.user;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructGlobalConfig.class)
public interface UserEntityMapper extends BaseEntityMapper<User, UserEntity> {
    
    @Override
    @Mapping(target = "isActive", defaultValue = "true")
    @Mapping(target = "isEmailVerified", defaultValue = "false")
    UserEntity toEntity(User domain);
    
    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "isEmailVerified", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updatePassword(@MappingTarget UserEntity entity, String encodedPassword);
}
