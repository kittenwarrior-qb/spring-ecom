package com.example.spring_ecom.config.security;

import com.example.spring_ecom.repository.database.permission.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom PermissionEvaluator for hasPermission() in @PreAuthorize
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final PermissionRepository permissionRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || permission == null) {
            return false;
        }
        
        String permissionName = permission.toString();
        return hasPermission(authentication, permissionName);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || permission == null) {
            return false;
        }
        
        String permissionName = permission.toString();
        return hasPermission(authentication, permissionName);
    }

    private boolean hasPermission(Authentication authentication, String permissionName) {
        // Check if user has the permission in their authorities
        Set<String> permissions = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        
        // Permission is stored as "PERMISSION_NAME" (without PREFIX_)
        boolean hasPermission = permissions.contains(permissionName);
        
        log.debug("Permission check: {} -> {}", permissionName, hasPermission);
        return hasPermission;
    }
}
