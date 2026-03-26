package com.example.spring_ecom.controller.api.admin.role;

import com.example.spring_ecom.controller.api.admin.model.AdminRoleResponseMapper;
import com.example.spring_ecom.controller.api.admin.model.PermissionResponse;
import com.example.spring_ecom.controller.api.admin.model.RoleResponse;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.domain.role.AssignPermissionsRequest;
import com.example.spring_ecom.service.role.RoleUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin - Role Management", description = "Admin APIs for managing roles and permissions")
public class AdminRoleController implements AdminRoleAPI {

    private final RoleUseCase roleUseCase;
    private final AdminRoleResponseMapper mapper;

    @Override
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        List<RoleResponse> roles = mapper.toRoleResponseList(roleUseCase.getAllRoles());
        return ResponseEntity.ok(ApiResponse.Success.of(roles));
    }

    @Override
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getAllPermissions() {
        List<PermissionResponse> permissions = mapper.toPermissionResponseList(roleUseCase.getAllPermissions());
        return ResponseEntity.ok(ApiResponse.Success.of(permissions));
    }

    @Override
    public ResponseEntity<ApiResponse<RoleResponse>> assignPermissions(Long roleId, AssignPermissionsRequest request) {
        RoleResponse response = mapper.toRoleResponse(
                roleUseCase.assignPermissionsToRole(roleId, request.permissionIds()));
        return ResponseEntity.ok(ApiResponse.Success.of(response));
    }

    @Override
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(com.example.spring_ecom.domain.role.CreateRoleRequest request) {
        RoleResponse response = mapper.toRoleResponse(roleUseCase.createRole(request));
        return ResponseEntity.ok(ApiResponse.Success.of(response));
    }

    @Override
    public ResponseEntity<ApiResponse<PermissionResponse>> createPermission(com.example.spring_ecom.domain.role.CreatePermissionRequest request) {
        PermissionResponse response = mapper.toPermissionResponse(roleUseCase.createPermission(request));
        return ResponseEntity.ok(ApiResponse.Success.of(response));
    }
}
