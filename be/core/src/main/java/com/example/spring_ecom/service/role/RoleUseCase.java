package com.example.spring_ecom.service.role;

import com.example.spring_ecom.domain.role.PermissionDto;
import com.example.spring_ecom.domain.role.RoleDto;

import java.util.List;
import java.util.Optional;

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

    // ========== Auth-related ==========

    /**
     * Find role ID by name (e.g., "USER", "ADMIN").
     */
    Optional<Long> findRoleIdByName(String name);

    /**
     * Build comma-separated authorities string for a user: "ROLE_ADMIN,PRODUCT_CREATE,..."
     */
    String buildAuthoritiesString(Long userId);
}
