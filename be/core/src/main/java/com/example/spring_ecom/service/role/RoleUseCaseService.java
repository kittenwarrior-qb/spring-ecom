package com.example.spring_ecom.service.role;

import com.example.spring_ecom.domain.role.PermissionDto;
import com.example.spring_ecom.domain.role.RoleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleUseCaseService implements RoleUseCase {

    private final RoleQueryService roleQueryService;
    private final RoleCommandService roleCommandService;

    @Override
    public List<RoleDto> getAllRoles() {
        return roleQueryService.getAllRoles();
    }

    @Override
    public List<PermissionDto> getAllPermissions() {
        return roleQueryService.getAllPermissions();
    }

    @Override
    public RoleDto assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        return roleCommandService.assignPermissionsToRole(roleId, permissionIds);
    }

    @Override
    public RoleDto createRole(com.example.spring_ecom.domain.role.CreateRoleRequest request) {
        return roleCommandService.createRole(request);
    }

    @Override
    public PermissionDto createPermission(com.example.spring_ecom.domain.role.CreatePermissionRequest request) {
        return roleCommandService.createPermission(request);
    }
    
    // User Role Management
    @Override
    public List<RoleDto> getUserRoles(Long userId) {
        return roleQueryService.getUserRoles(userId);
    }
    
    @Override
    public void addRoleToUser(Long userId, Long roleId) {
        roleCommandService.addRoleToUser(userId, roleId);
    }
    
    @Override
    public void removeRoleFromUser(Long userId, Long roleId) {
        roleCommandService.removeRoleFromUser(userId, roleId);
    }
    
    @Override
    public void setUserRoles(Long userId, List<Long> roleIds) {
        roleCommandService.setUserRoles(userId, roleIds);
    }

    @Override
    public Optional<Long> findRoleIdByName(String name) {
        return roleQueryService.findRoleIdByName(name);
    }

    @Override
    public String buildAuthoritiesString(Long userId) {
        return roleQueryService.buildAuthoritiesString(userId);
    }
}
