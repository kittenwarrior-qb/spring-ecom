package com.example.spring_ecom.controller.api.user;

import com.example.spring_ecom.controller.api.user.model.UserResponse;
import com.example.spring_ecom.core.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("v1/api/user/{userId}")
public interface UserAPI {

    @GetMapping
    ApiResponse<UserResponse> findById(@PathVariable String userId);

}