package com.example.spring_ecom.controller.api.admin.user;

import java.util.List;

public record SetUserRolesRequest(
    List<Long> roleIds
) {}
