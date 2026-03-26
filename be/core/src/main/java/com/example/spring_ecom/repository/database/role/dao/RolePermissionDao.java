package com.example.spring_ecom.repository.database.role.dao;

/**
 * DAO for fetching role-permission mapping in a single query
 * Used to avoid N+1 query problem
 * 
 * Each row represents: roleId, roleName, permissionName (or null if no permission)
 */
public record RolePermissionDao(
    Long roleId,
    String roleName,
    String permissionName  // null if role has no permissions
) {}
