package com.example.spring_ecom.controller.api.auth;

import com.example.spring_ecom.controller.api.auth.model.AuthResponse;
import com.example.spring_ecom.controller.api.auth.model.LoginRequest;
import com.example.spring_ecom.controller.api.auth.model.RegisterRequest;
import com.example.spring_ecom.core.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("v1/api/auth")
public interface AuthAPI {

    @GetMapping("/login")
    ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request);

    @GetMapping("/register")
    ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request);
}
