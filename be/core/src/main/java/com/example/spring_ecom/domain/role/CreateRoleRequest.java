package com.example.spring_ecom.domain.role;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record CreateRoleRequest(
        @NotBlank(message = "Role name is required")
        String name,
        List<Long> permissionIds
) {}
