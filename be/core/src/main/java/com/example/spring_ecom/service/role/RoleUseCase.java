package com.example.spring_ecom.service.role;

import com.example.spring_ecom.domain.role.PermissionDto;
import com.example.spring_ecom.domain.role.RoleDto;

import java.util.List;

public interface RoleUseCase {

    List<RoleDto> getAllRoles();

    List<PermissionDto> getAllPermissions();

    RoleDto assignPermissionsToRole(Long roleId, List<Long> permissionIds);

    RoleDto createRole(com.example.spring_ecom.domain.role.CreateRoleRequest request);

    PermissionDto createPermission(com.example.spring_ecom.domain.role.CreatePermissionRequest request);
    
    // User Role Management
    List<RoleDto> getUserRoles(Long userId);
    
    void addRoleToUser(Long userId, Long roleId);
    
    void removeRoleFromUser(Long userId, Long roleId);
    
    void setUserRoles(Long userId, List<Long> roleIds);
}
