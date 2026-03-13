package com.example.spring_ecom.controller.api.user.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.domain.user.UserRole;
import com.example.spring_ecom.domain.userInfo.UserInfo;
import com.example.spring_ecom.repository.redis.session.RedisEntity;
import com.example.spring_ecom.service.userInfo.UserInfoUseCase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(config = MapStructGlobalConfig.class)
public abstract class UserProfileResponseMapper {
    
    @Autowired
    protected UserInfoUseCase userInfoUseCase;
    
    @Mapping(target = "firstName", expression = "java(getUserInfo(user).firstName())")
    @Mapping(target = "lastName", expression = "java(getUserInfo(user).lastName())")
    @Mapping(target = "phoneNumber", expression = "java(getUserInfo(user).phoneNumber())")
    @Mapping(target = "dateOfBirth", expression = "java(getUserInfo(user).dateOfBirth())")
    @Mapping(target = "avatarUrl", expression = "java(getUserInfo(user).avatarUrl())")
    @Mapping(target = "address", expression = "java(getUserInfo(user).address())")
    @Mapping(target = "ward", expression = "java(getUserInfo(user).ward())")
    @Mapping(target = "district", expression = "java(getUserInfo(user).district())")
    @Mapping(target = "city", expression = "java(getUserInfo(user).city())")
    @Mapping(target = "postalCode", expression = "java(getUserInfo(user).postalCode())")
    public abstract UserProfileResponse toResponse(User user);
    
    @Mapping(target = "id", source = "userId")
    @Mapping(target = "dateOfBirth", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "postalCode", ignore = true)
    @Mapping(target = "role", source = "role")
    @Mapping(target = "isEmailVerified", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "updatedAt", source = "lastAccessedAt")
    public abstract UserProfileResponse fromSessionToUserProfileResponse(RedisEntity session);
    
    protected UserInfo getUserInfo(User user) {
        if (user == null || user.id() == null) {
            return new UserInfo(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        }
        return userInfoUseCase.findByUserId(user.id()).orElse(
            new UserInfo(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
        );
    }
}