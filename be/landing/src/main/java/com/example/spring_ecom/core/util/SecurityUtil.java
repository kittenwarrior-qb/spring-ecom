package com.example.spring_ecom.core.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SecurityUtil {
    
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
    
    public static Long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            Object userId = attributes.getRequest().getAttribute("userId");
            if (userId instanceof Long) {
                return (Long) userId;
            }
        }
        return null;
    }
    
    public static String getCurrentSessionId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            Object sessionId = attributes.getRequest().getAttribute("sessionId");
            if (sessionId instanceof String) {
                return (String) sessionId;
            }
        }
        return null;
    }
    
    /**
     * Check if current user has a specific role (supports multiple roles)
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + role));
        }
        return false;
    }
    
    /**
     * Check if current user has any of the specified roles
     */
    public static boolean hasAnyRole(String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            Set<String> userRoles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(auth -> auth.startsWith("ROLE_"))
                    .collect(Collectors.toSet());
            
            for (String role : roles) {
                if (userRoles.contains("ROLE_" + role)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Check if current user has a specific permission
     */
    public static boolean hasPermission(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(permission));
        }
        return false;
    }
    
    /**
     * Check if current user has any of the specified permissions
     */
    public static boolean hasAnyPermission(String... permissions) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            Set<String> userPermissions = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(auth -> !auth.startsWith("ROLE_"))
                    .collect(Collectors.toSet());
            
            for (String permission : permissions) {
                if (userPermissions.contains(permission)) {
                    return true;
                }
            }
        }
        return false;
    }
}
