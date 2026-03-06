package com.example.spring_ecom.controller.api.user;

import com.example.spring_ecom.controller.api.user.model.UserResponse;
import com.example.spring_ecom.core.response.ValueResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("v1/api/users/{userId}")
public interface UserAPI {

    @GetMapping
    ValueResponse<UserResponse> findById(@PathVariable String userId);

}