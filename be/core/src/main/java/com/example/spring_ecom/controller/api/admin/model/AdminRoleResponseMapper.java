package com.example.spring_ecom.controller.api.admin.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.domain.role.PermissionDto;
import com.example.spring_ecom.domain.role.RoleDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructGlobalConfig.class)
public interface AdminRoleResponseMapper {

    RoleResponse toRoleResponse(RoleDto roleDto);

    PermissionResponse toPermissionResponse(PermissionDto permissionDto);

    List<RoleResponse> toRoleResponseList(List<RoleDto> roleDtos);

    List<PermissionResponse> toPermissionResponseList(List<PermissionDto> permissionDtos);
}
