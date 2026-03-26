package com.example.spring_ecom.service.role;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.role.PermissionDto;
import com.example.spring_ecom.domain.role.RoleDto;
import com.example.spring_ecom.repository.database.permission.PermissionEntity;
import com.example.spring_ecom.repository.database.permission.PermissionRepository;
import com.example.spring_ecom.repository.database.role.RoleEntity;
import com.example.spring_ecom.repository.database.role.RolePermissionEntity;
import com.example.spring_ecom.repository.database.role.RolePermissionRepository;
import com.example.spring_ecom.repository.database.role.RoleRepository;
import com.example.spring_ecom.repository.database.user.UserRoleEntity;
import com.example.spring_ecom.repository.database.user.UserRoleRepository;
import com.example.spring_ecom.repository.database.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleCommandService {

    private static final String ADMIN_ROLE_NAME = "ADMIN";
    private static final String SUPERADMIN_ROLE_NAME = "SUPERADMIN";
    private static final String ROLE_ADMIN_MANAGE_PERMISSION = "ROLE_ADMIN_MANAGE";
    
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;

    @Transactional
    public RoleDto assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Role not found"));

        // Validate that all provided permission IDs exist
        List<PermissionEntity> permissions = permissionRepository.findAllById(permissionIds);
        if (permissions.size() != permissionIds.size()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "One or more permission IDs are invalid");
        }

        // Delete all existing mappings for this role, then re-insert
        List<Long> existingPermIds = rolePermissionRepository.findPermissionIdsByRoleId(roleId);
        existingPermIds.forEach(permId -> {
            rolePermissionRepository.deleteById(
                    new com.example.spring_ecom.repository.database.role.RolePermissionId(roleId, permId));
        });

        // Insert new mappings
        permissionIds.forEach(permId -> {
            RolePermissionEntity rp = RolePermissionEntity.builder()
                    .roleId(roleId)
                    .permissionId(permId)
                    .build();
            rolePermissionRepository.save(rp);
        });

        List<String> permissionNames = permissions.stream()
                .map(PermissionEntity::getName)
                .toList();

        return new RoleDto(role.getId(), role.getName(), permissionNames);
    }

    @Transactional
    public RoleDto createRole(com.example.spring_ecom.domain.role.CreateRoleRequest request) {
        if (roleRepository.findByName(request.name()).isPresent()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Role already exists");
        }
        RoleEntity newRole = RoleEntity.builder().name(request.name()).build();
        newRole = roleRepository.save(newRole);
        if (request.permissionIds() != null && !request.permissionIds().isEmpty()) {
            return assignPermissionsToRole(newRole.getId(), request.permissionIds());
        }
        return new RoleDto(newRole.getId(), newRole.getName(), List.of());
    }

    @Transactional
    public PermissionDto createPermission(com.example.spring_ecom.domain.role.CreatePermissionRequest request) {
        if (permissionRepository.findByName(request.name()).isPresent()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Permission already exists");
        }
        PermissionEntity newPermission = PermissionEntity.builder().name(request.name()).build();
        newPermission = permissionRepository.save(newPermission);
        return new PermissionDto(newPermission.getId(), newPermission.getName());
    }
    
    // User Role Management
    
    @Transactional
    public void addRoleToUser(Long userId, Long roleId) {
        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "User not found"));
        
        // Validate role exists
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Role not found"));
        
        // Nobody can assign SUPERADMIN role to others
        if (SUPERADMIN_ROLE_NAME.equals(role.getName())) {
            throw new BaseException(ResponseCode.FORBIDDEN, 
                    "Cannot assign SUPERADMIN role to others. Only one SUPERADMIN is allowed.");
        }
        
        // Check if trying to assign ADMIN role - requires ROLE_ADMIN_MANAGE permission
        if (ADMIN_ROLE_NAME.equals(role.getName())) {
            checkAdminManagePermission();
        }
        
        // Check if already assigned
        if (userRoleRepository.existsByUserIdAndRoleId(userId, roleId)) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "User already has this role");
        }
        
        UserRoleEntity userRole = UserRoleEntity.builder()
                .userId(userId)
                .roleId(roleId)
                .build();
        userRoleRepository.save(userRole);
    }
    
    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
    }
    
    @Transactional
    public void setUserRoles(Long userId, List<Long> roleIds) {
        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "User not found"));
        
        // Validate all roles exist and check for restricted roles
        boolean hasAdminRole = false;
        boolean hasSuperAdminRole = false;
        for (Long roleId : roleIds) {
            RoleEntity role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Role not found: " + roleId));
            if (ADMIN_ROLE_NAME.equals(role.getName())) {
                hasAdminRole = true;
            }
            if (SUPERADMIN_ROLE_NAME.equals(role.getName())) {
                hasSuperAdminRole = true;
            }
        }
        
        // Nobody can assign SUPERADMIN role to others (only one SUPERADMIN allowed)
        if (hasSuperAdminRole) {
            // Check if the target user is already SUPERADMIN
            boolean isAlreadySuperAdmin = userRoleRepository.findByUserId(userId).stream()
                    .anyMatch(ur -> {
                        RoleEntity r = roleRepository.findById(ur.getRoleId()).orElse(null);
                        return r != null && SUPERADMIN_ROLE_NAME.equals(r.getName());
                    });
            if (!isAlreadySuperAdmin) {
                throw new BaseException(ResponseCode.FORBIDDEN, 
                        "Cannot assign SUPERADMIN role to others. Only one SUPERADMIN is allowed.");
            }
        }
        
        // If trying to assign ADMIN role, check permission
        if (hasAdminRole) {
            checkAdminManagePermission();
        }
        
        // Remove all existing roles
        userRoleRepository.deleteByUserId(userId);
        
        // Add new roles
        roleIds.forEach(roleId -> {
            UserRoleEntity userRole = UserRoleEntity.builder()
                    .userId(userId)
                    .roleId(roleId)
                    .build();
            userRoleRepository.save(userRole);
        });
    }
    
    /**
     * Check if current user has permission to manage ADMIN role assignment.
     * Only SUPERADMIN (or anyone with ROLE_ADMIN_MANAGE permission) can assign ADMIN role.
     */
    private void checkAdminManagePermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "Authentication required");
        }
        
        boolean hasPermission = authentication.getAuthorities().stream()
                .anyMatch(auth -> ROLE_ADMIN_MANAGE_PERMISSION.equals(auth.getAuthority()));
        
        if (!hasPermission) {
            throw new BaseException(ResponseCode.FORBIDDEN, 
                    "You don't have permission to assign ADMIN role. Only SUPERADMIN can do this.");
        }
    }
}
