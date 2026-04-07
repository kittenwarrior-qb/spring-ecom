package com.example.spring_ecom.service.auth;

import com.example.spring_ecom.controller.api.auth.model.LoginResponse;
import com.example.spring_ecom.domain.auth.LoginDto;
import com.example.spring_ecom.domain.auth.RegisterDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthUseCase {
    LoginResponse login(LoginDto command, HttpServletRequest request, HttpServletResponse response);
    LoginResponse register(RegisterDto command, HttpServletRequest request, HttpServletResponse response);
    LoginResponse refreshToken(String refreshToken, HttpServletRequest request, HttpServletResponse response);
    void logout(String refreshToken);
    void logoutBySessionId(String sessionId);
    boolean isEmailVerified(String email);
}
