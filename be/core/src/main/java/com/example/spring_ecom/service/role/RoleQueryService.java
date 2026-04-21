package com.example.spring_ecom.service.role;

import com.example.spring_ecom.domain.role.PermissionDto;
import com.example.spring_ecom.domain.role.RoleDto;
import com.example.spring_ecom.repository.database.permission.PermissionEntity;
import com.example.spring_ecom.repository.database.permission.PermissionRepository;
import com.example.spring_ecom.repository.database.role.RoleEntityMapper;
import com.example.spring_ecom.repository.database.role.dao.RolePermissionDao;
import com.example.spring_ecom.repository.database.role.RolePermissionRepository;
import com.example.spring_ecom.repository.database.role.RoleRepository;
import com.example.spring_ecom.repository.database.user.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleQueryService {

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final RoleEntityMapper roleEntityMapper;
    private final UserRoleRepository userRoleRepository;

    /**
     * Optimized: Single query to get all roles with permissions
     * Before: 1 + 2N queries (N = number of roles)
     * After: 1 query
     */
    public List<RoleDto> getAllRoles() {
        List<RolePermissionDao> results = roleRepository.findAllRolePermissions();
        
        // Group by roleId in Java
        Map<Long, List<String>> rolePermissions = new LinkedHashMap<>();
        Map<Long, String> roleNames = new LinkedHashMap<>();
        
        for (RolePermissionDao dao : results) {
            roleNames.putIfAbsent(dao.roleId(), dao.roleName());
            if (Objects.nonNull(dao.permissionName())) {
                rolePermissions.computeIfAbsent(dao.roleId(), k -> new ArrayList<>())
                        .add(dao.permissionName());
            }
        }
        
        return roleNames.entrySet().stream()
                .map(e -> new RoleDto(
                        e.getKey(),
                        e.getValue(),
                        rolePermissions.getOrDefault(e.getKey(), List.of())
                ))
                .toList();
    }

    public List<PermissionDto> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(roleEntityMapper::toPermissionDto)
                .toList();
    }
    
    /**
     * Optimized: Batch query to get user roles with permissions
     * Before: 1 + 3N queries (N = number of roles)
     * After: 2 queries (1 for roleIds, 1 for all role-permission mappings)
     */
    public List<RoleDto> getUserRoles(Long userId) {
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return List.of();
        }
        
        // Single query to get all permissions for all roles
        List<RolePermissionDao> results = roleRepository.findAllRolePermissions();
        
        // Group by roleId and filter only user's roles
        Map<Long, List<String>> rolePermissions = new LinkedHashMap<>();
        Map<Long, String> roleNames = new LinkedHashMap<>();
        
        for (RolePermissionDao dao : results) {
            if (roleIds.contains(dao.roleId())) {
                roleNames.putIfAbsent(dao.roleId(), dao.roleName());
                if (Objects.nonNull(dao.permissionName())) {
                    rolePermissions.computeIfAbsent(dao.roleId(), k -> new ArrayList<>())
                            .add(dao.permissionName());
                }
            }
        }
        
        return roleNames.entrySet().stream()
                .map(e -> new RoleDto(
                        e.getKey(),
                        e.getValue(),
                        rolePermissions.getOrDefault(e.getKey(), List.of())
                ))
                .toList();
    }

    // ========== Auth-related queries ==========

    public Optional<Long> findRoleIdByName(String name) {
        return roleRepository.findByName(name).map(r -> r.getId());
    }

    public String buildAuthoritiesString(Long userId) {
        List<RoleDto> roles = getUserRoles(userId);
        List<String> authorities = new ArrayList<>();
        for (RoleDto role : roles) {
            authorities.add("ROLE_" + role.name());
            authorities.addAll(role.permissions());
        }
        return String.join(",", authorities);
    }
}
