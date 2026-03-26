package com.example.spring_ecom.repository.database.auth;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.domain.auth.RegisterDto;
import com.example.spring_ecom.repository.database.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface AuthEntityMapper {
    
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "isEmailVerified", constant = "false")
    UserEntity toEntity(RegisterDto command);
}
