package com.example.spring_ecom.controller.api.user.userInfo.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.domain.userInfo.UserInfo;
import com.example.spring_ecom.repository.database.permission.PermissionRepository;
import com.example.spring_ecom.repository.database.role.RoleRepository;
import com.example.spring_ecom.repository.database.user.UserRoleRepository;
import com.example.spring_ecom.repository.redis.session.RedisEntity;
import com.example.spring_ecom.service.userInfo.UserInfoUseCase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mapper(config = MapStructGlobalConfig.class)
public abstract class UserInfoResponseMapper {
    
    @Autowired
    protected UserInfoUseCase userInfoUseCase;
    
    @Autowired
    protected PermissionRepository permissionRepository;
    
    @Autowired
    protected UserRoleRepository userRoleRepository;
    
    @Autowired
    protected RoleRepository roleRepository;
    
    private static final Logger log = LoggerFactory.getLogger(UserInfoResponseMapper.class);
    
    @Mapping(target = "firstName", expression = "java(getUserInfo(user).firstName())")
    @Mapping(target = "lastName", expression = "java(getUserInfo(user).lastName())")
    @Mapping(target = "phoneNumber", expression = "java(getUserInfo(user).phoneNumber())")
    @Mapping(target = "dateOfBirth", expression = "java(getUserInfo(user).dateOfBirth())")
    @Mapping(target = "avatarUrl", expression = "java(getUserInfo(user).avatarUrl())")
    @Mapping(target = "address", expression = "java(getUserInfo(user).address())")
    @Mapping(target = "ward", expression = "java(getUserInfo(user).ward())")
    @Mapping(target = "district", expression = "java(getUserInfo(user).district())")
    @Mapping(target = "city", expression = "java(getUserInfo(user).city())")
    @Mapping(target = "postalCode", expression = "java(getUserInfo(user).postalCode())")
    @Mapping(target = "roleName", ignore = true)
    @Mapping(target = "roles", expression = "java(getRoles(user.id()))")
    @Mapping(target = "permissions", expression = "java(getPermissions(user.id()))")
    public abstract UserInfoResponse toResponse(User user);
    
    @Mapping(target = "id", source = "userId")
    @Mapping(target = "dateOfBirth", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "postalCode", ignore = true)
    @Mapping(target = "roleName", source = "role")
    @Mapping(target = "roles", expression = "java(getRoles(session.getUserId()))")
    @Mapping(target = "permissions", expression = "java(getPermissions(session.getUserId()))")
    @Mapping(target = "isEmailVerified", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "updatedAt", source = "lastAccessedAt")
    public abstract UserInfoResponse sessionToResponse(RedisEntity session);
    
    protected UserInfo getUserInfo(User user) {
        if (user == null || user.id() == null) {
            return new UserInfo(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        }
        return userInfoUseCase.findByUserId(user.id()).orElse(
            new UserInfo(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
        );
    }
    
    protected Set<String> getPermissions(Long userId) {
        if (userId == null) {
            log.warn("getPermissions: userId is null");
            return new HashSet<>();
        }
        
        log.debug("getPermissions: Loading permissions for userId={}", userId);
        Set<String> permissions = new HashSet<>();
        
        try {
            // Use optimized query to get all permissions for user directly
            List<String> permissionNames = permissionRepository.findPermissionsByUserId(userId);
            permissions.addAll(permissionNames);
            
            log.info("getPermissions: Loaded {} permissions for userId={}", permissions.size(), userId);
        } catch (Exception e) {
            log.error("getPermissions: Error loading permissions for userId={}", userId, e);
        }
        
        return permissions;
    }
    
    protected Set<String> getRoles(Long userId) {
        if (userId == null) {
            log.warn("getRoles: userId is null");
            return new HashSet<>();
        }
        
        log.debug("getRoles: Loading roles for userId={}", userId);
        Set<String> roles = new HashSet<>();
        
        try {
            // Get all role IDs for user
            List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
            
            // Get role names for each role ID
            for (Long roleId : roleIds) {
                roleRepository.findNameById(roleId).ifPresent(roles::add);
            }
            
            log.info("getRoles: Loaded {} roles for userId={}: {}", roles.size(), userId, roles);
        } catch (Exception e) {
            log.error("getRoles: Error loading roles for userId={}", userId, e);
        }
        
        return roles;
    }
}