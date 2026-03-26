package com.example.spring_ecom.controller.api.user.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.repository.database.role.RoleRepository;
import com.example.spring_ecom.repository.database.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(config = MapStructGlobalConfig.class, componentModel = "spring")
public abstract class UserResponseMapper {
    
    @Autowired
    protected RoleRepository roleRepository;
    
    @Mapping(target = "firstName", source = "userInfo.firstName")
    @Mapping(target = "lastName", source = "userInfo.lastName")
    @Mapping(target = "phoneNumber", source = "userInfo.phoneNumber")
    @Mapping(target = "dateOfBirth", source = "userInfo.dateOfBirth")
    @Mapping(target = "avatarUrl", source = "userInfo.avatarUrl")
    @Mapping(target = "roles", source = "id", qualifiedByName = "getRolesByUserId")
    public abstract UserResponse entityToResponse(UserEntity userEntity);
    
    @Named("getRolesByUserId")
    protected List<String> getRolesByUserId(Long userId) {
        return roleRepository.findRoleNamesByUserId(userId);
    }
    
    @Mapping(target = "roles", ignore = true)
    public abstract UserResponse toResponse(User user);
}
