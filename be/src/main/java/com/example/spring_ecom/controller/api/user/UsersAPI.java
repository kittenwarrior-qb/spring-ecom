package com.example.spring_ecom.controller.api.user;

import com.example.spring_ecom.controller.api.user.model.UserRequest;
import com.example.spring_ecom.core.response.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("v1/api/users")
public interface UsersAPI {

    @PostMapping
    ApiResponse<Void> save(@RequestBody UserRequest request);
}
