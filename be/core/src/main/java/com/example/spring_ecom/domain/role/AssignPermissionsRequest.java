package com.example.spring_ecom.domain.role;

import java.util.List;

public record AssignPermissionsRequest(
        List<Long> permissionIds
) {}
