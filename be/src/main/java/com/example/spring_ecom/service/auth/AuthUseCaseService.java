package com.example.spring_ecom.service.auth;

import com.example.spring_ecom.controller.api.auth.model.AuthResponse;
import com.example.spring_ecom.core.util.CookieUtil;
import com.example.spring_ecom.domain.auth.LoginDto;
import com.example.spring_ecom.domain.auth.RegisterDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthUseCaseService implements AuthUseCase {
    private final AuthCommandService commandService;
    private final CookieUtil cookieUtil;

    @Override
    @Transactional
    public AuthResponse login(LoginDto command, HttpServletRequest request, HttpServletResponse response) {
        String deviceInfo = request.getHeader("User-Agent");
        String ipAddress = request.getRemoteAddr();
        return commandService.login(command, deviceInfo, ipAddress, response);
    }
    
    @Override
    @Transactional
    public AuthResponse register(RegisterDto command, HttpServletRequest request, HttpServletResponse response) {
        String deviceInfo = request.getHeader("User-Agent");
        String ipAddress = request.getRemoteAddr();
        return commandService.register(command, deviceInfo, ipAddress, response);
    }
    
    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        String deviceInfo = request.getHeader("User-Agent");
        String ipAddress = request.getRemoteAddr();
        return commandService.refreshToken(refreshToken, deviceInfo, ipAddress, response);
    }
    
    @Override
    @Transactional
    public void logout(String refreshToken) {
        commandService.logout(refreshToken);
    }
    
    @Override
    @Transactional
    public void logoutBySessionId(String sessionId) {
        commandService.logoutBySessionId(sessionId);
    }
}
