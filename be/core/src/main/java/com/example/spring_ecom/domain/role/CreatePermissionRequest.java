package com.example.spring_ecom.domain.role;

import jakarta.validation.constraints.NotBlank;

public record CreatePermissionRequest(
        @NotBlank(message = "Permission name is required")
        String name
) {}
