package com.example.spring_ecom.controller.api.user.model;

import jakarta.validation.constraints.NotNull;

public record AssignRoleRequest(
        @NotNull(message = "Role ID is required")
        Long roleId
) {}
