package com.example.spring_ecom.controller.api.user;

import com.example.spring_ecom.controller.api.user.model.UserRequest;
import com.example.spring_ecom.controller.api.user.model.UserResponse;
import com.example.spring_ecom.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "User management APIs")
@RequestMapping("v1/api/users")
public interface UsersAPI {

    @PostMapping
    ApiResponse<Void> save(@RequestBody UserRequest request);

    @GetMapping
    ApiResponse<Page<UserResponse>> findAll(Pageable pageable);
}
