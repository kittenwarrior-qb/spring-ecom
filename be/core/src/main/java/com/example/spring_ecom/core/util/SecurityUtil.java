package com.example.spring_ecom.core.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Component
public class SecurityUtil {
    
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
    
    public static Long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(attributes)) {
            Object userId = attributes.getRequest().getAttribute("userId");
            if (userId instanceof Long) {
                return (Long) userId;
            }
        }
        return null;
    }
    
    public static String getCurrentSessionId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(attributes)) {
            Object sessionId = attributes.getRequest().getAttribute("sessionId");
            if (sessionId instanceof String) {
                return (String) sessionId;
            }
        }
        return null;
    }
    
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && Objects.nonNull(authentication.getAuthorities())) {
            return authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + role));
        }
        return false;
    }
}
