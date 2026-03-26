package com.example.spring_ecom.controller.api.admin.model;

import java.util.List;

public record RoleResponse(
        Long id,
        String name,
        List<String> permissions
) {}
