package com.example.spring_ecom.controller.api.auth.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.domain.auth.LoginDto;
import com.example.spring_ecom.domain.auth.RegisterDto;
import org.mapstruct.Mapper;

@Mapper(config = MapStructGlobalConfig.class)
public interface AuthRequestMapper {
    
    LoginDto toDomain(LoginRequest request);
    
    RegisterDto toDomain(RegisterRequest request);
}
