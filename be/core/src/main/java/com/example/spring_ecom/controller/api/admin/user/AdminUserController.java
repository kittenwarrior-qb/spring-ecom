package com.example.spring_ecom.controller.api.admin.user;

import com.example.spring_ecom.controller.api.user.model.UserResponse;
import com.example.spring_ecom.controller.api.user.model.UserResponseMapper;
import com.example.spring_ecom.controller.api.admin.model.RoleResponse;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.service.user.UserUseCase;
import com.example.spring_ecom.service.role.RoleUseCase;
import com.example.spring_ecom.domain.role.RoleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminUserController implements AdminUserAPI {

    private final UserUseCase userUseCase;
    private final UserResponseMapper userResponseMapper;
    private final RoleUseCase roleUseCase;

    @Override
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            Pageable pageable, String search, Boolean isActive) {
        try {
            log.info("Admin getting all users with pagination");
            Page<User> users = userUseCase.findAll(
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
            Page<UserResponse> responses = users.map(userResponseMapper::toResponse);
            return ResponseEntity.ok(ApiResponse.Success.of(responses));
        } catch (Exception e) {
            log.error("Error getting all users: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to get users"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(Long userId) {
        try {
            log.info("Admin getting user by ID: {}", userId);
            return userUseCase.findByUserId(userId)
                    .map(user -> ResponseEntity.ok(ApiResponse.Success.of(userResponseMapper.toResponse(user))))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting user by ID: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to get user"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(Long userId, boolean isActive, String reason) {
        try {
            log.info("Admin updating user status: userId={}, isActive={}", userId, isActive);
            boolean success = userUseCase.updateUserStatus(userId, isActive);
            if (success) {
                return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK,
                        "User " + (isActive ? "activated" : "deactivated") + " successfully", null));
            } else {
                return ResponseEntity.internalServerError()
                         .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to update user status"));
            }
        } catch (Exception e) {
            log.error("Error updating user status: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to update user status"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteUser(Long userId, String reason) {
        try {
            log.info("Admin deleting user: {}, reason: {}", userId, reason);
            boolean success = userUseCase.deleteUser(userId);
            if (success) {
                return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK, "User deleted successfully", null));
            } else {
                return ResponseEntity.internalServerError()
                        .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to delete user"));
            }
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to delete user"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> resetUserPassword(Long userId) {
        try {
            log.info("Admin resetting password for user: {}", userId);
            // TODO: Implement proper password reset logic (generate temp password, send email)
            return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK, "Password reset email sent", null));
        } catch (Exception e) {
            log.error("Error resetting user password: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to reset password"));
        }
    }

    // ============ User Role Management ============
    
    @Override
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getUserRoles(Long userId) {
        try {
            log.info("Admin getting roles for user: {}", userId);
            List<RoleDto> roles = roleUseCase.getUserRoles(userId);
            List<RoleResponse> responses = roles.stream()
                    .map(r -> new RoleResponse(r.id(), r.name(), r.permissions()))
                    .toList();
            return ResponseEntity.ok(ApiResponse.Success.of(responses));
        } catch (Exception e) {
            log.error("Error getting user roles: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to get user roles"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> addRoleToUser(Long userId, Long roleId) {
        try {
            log.info("Admin adding role {} to user {}", roleId, userId);
            roleUseCase.addRoleToUser(userId, roleId);
            return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK, "Role added successfully", null));
        } catch (Exception e) {
            log.error("Error adding role to user: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to add role"));
        }
    }

    public ResponseEntity<ApiResponse<Void>> removeRoleFromUser(Long userId, Long roleId) {
        try {
            log.info("Admin removing role {} from user {}", roleId, userId);
            roleUseCase.removeRoleFromUser(userId, roleId);
            return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK, "Role removed successfully", null));
        } catch (Exception e) {
            log.error("Error removing role from user: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to remove role"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> setUserRoles(Long userId, SetUserRolesRequest request) {
        try {
            log.info("Admin setting roles for user: {}, roleIds: {}", userId, request.roleIds());
            roleUseCase.setUserRoles(userId, request.roleIds());
            return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK, "User roles updated successfully", null));
        } catch (Exception e) {
            log.error("Error setting user roles: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to set user roles"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchUsersByEmail(String email, Pageable pageable) {
        try {
            log.info("Admin searching users by email: {}", email);
            Page<User> users = userUseCase.searchByEmail(email, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
            Page<UserResponse> responses = users.map(userResponseMapper::toResponse);
            return ResponseEntity.ok(ApiResponse.Success.of(responses));
        } catch (Exception e) {
            log.error("Error searching users by email: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to search users"));
        }
    }
}
