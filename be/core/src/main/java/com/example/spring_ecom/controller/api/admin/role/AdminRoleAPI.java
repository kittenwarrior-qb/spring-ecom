package com.example.spring_ecom.controller.api.admin.role;

import com.example.spring_ecom.controller.api.admin.model.PermissionResponse;
import com.example.spring_ecom.controller.api.admin.model.RoleResponse;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.domain.role.AssignPermissionsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/api/admin/roles")
@Tag(name = "Admin - Role Management", description = "Admin APIs for managing roles and permissions")
public interface AdminRoleAPI {

    @GetMapping
    @Operation(summary = "Get all roles with their permissions")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles();

    @GetMapping("/permissions")
    @Operation(summary = "Get all available permissions")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    ResponseEntity<ApiResponse<List<PermissionResponse>>> getAllPermissions();

    @PutMapping("/{roleId}/permissions")
    @Operation(summary = "Assign a set of permissions to a role (replaces existing)")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    ResponseEntity<ApiResponse<RoleResponse>> assignPermissions(
            @PathVariable Long roleId,
            @RequestBody AssignPermissionsRequest request);

    @PostMapping
    @Operation(summary = "Create a new role")
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    ResponseEntity<ApiResponse<RoleResponse>> createRole(@RequestBody com.example.spring_ecom.domain.role.CreateRoleRequest request);

    @PostMapping("/permissions")
    @Operation(summary = "Create a new permission")
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    ResponseEntity<ApiResponse<PermissionResponse>> createPermission(@RequestBody com.example.spring_ecom.domain.role.CreatePermissionRequest request);
}
