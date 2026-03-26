package com.example.spring_ecom.repository.database.role;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.domain.role.PermissionDto;
import com.example.spring_ecom.repository.database.permission.PermissionEntity;
import org.mapstruct.Mapper;

@Mapper(config = MapStructGlobalConfig.class)
public interface RoleEntityMapper {

    PermissionDto toPermissionDto(PermissionEntity entity);
}
