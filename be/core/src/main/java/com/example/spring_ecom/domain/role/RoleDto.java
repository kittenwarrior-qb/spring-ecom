package com.example.spring_ecom.domain.role;

import java.util.List;

public record RoleDto(
        Long id,
        String name,
        List<String> permissions
) {}
