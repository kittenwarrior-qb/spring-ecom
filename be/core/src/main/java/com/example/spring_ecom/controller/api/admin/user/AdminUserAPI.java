package com.example.spring_ecom.controller.api.admin.user;

import com.example.spring_ecom.controller.api.user.model.UserResponse;
import com.example.spring_ecom.controller.api.admin.model.RoleResponse;
import com.example.spring_ecom.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/api/admin/users")
@Tag(name = "Admin User Management", description = "Admin APIs for managing users")
public interface AdminUserAPI {

    @Operation(summary = "Get all users", description = "Get paginated list of all users")
    @GetMapping
    @PreAuthorize("hasAuthority('USER_VIEW')")
    ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            Pageable pageable,
            @Parameter(description = "Search term") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean isActive);

    @Operation(summary = "Get user by ID", description = "Retrieve user information by user ID")
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @Parameter(description = "User ID") @PathVariable Long userId);

    @Operation(summary = "Update user status", description = "Activate or deactivate user")
    @PutMapping("/{userId}/status")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @RequestParam boolean isActive,
            @RequestParam(required = false) String reason);

    @Operation(summary = "Delete user", description = "Soft delete user")
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Reason for deletion") @RequestParam(required = false) String reason);

    @Operation(summary = "Reset user password", description = "Reset user password to a temporary one")
    @PostMapping("/{userId}/reset-password")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    ResponseEntity<ApiResponse<Void>> resetUserPassword(
            @Parameter(description = "User ID") @PathVariable Long userId);

    // ============ User Role Management ============
    
    @Operation(summary = "Get user's roles", description = "Get all roles assigned to a user")
    @GetMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    ResponseEntity<ApiResponse<List<RoleResponse>>> getUserRoles(
            @Parameter(description = "User ID") @PathVariable Long userId);

    @Operation(summary = "Add role to user", description = "Assign an additional role to a user")
    @PostMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    ResponseEntity<ApiResponse<Void>> addRoleToUser(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Role ID") @PathVariable Long roleId);

    @Operation(summary = "Set user roles", description = "Replace all roles for a user")
    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    ResponseEntity<ApiResponse<Void>> setUserRoles(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @RequestBody SetUserRolesRequest request);

    @Operation(summary = "Search users by email", description = "Search users by email containing the given string")
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    ResponseEntity<ApiResponse<Page<UserResponse>>> searchUsersByEmail(
            @Parameter(description = "Email search term") @RequestParam String email,
            Pageable pageable);
}
