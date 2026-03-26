package com.example.spring_ecom.controller.api.user.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.repository.database.role.RoleRepository;
import com.example.spring_ecom.repository.database.user.UserEntity;
import com.example.spring_ecom.repository.database.user.UserRoleRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(config = MapStructGlobalConfig.class)
public abstract class UserResponseMapper {
    
    @Autowired
    protected UserRoleRepository userRoleRepository;
    
    @Autowired
    protected RoleRepository roleRepository;
    
    @Mapping(target = "firstName", source = "userInfo.firstName")
    @Mapping(target = "lastName", source = "userInfo.lastName")
    @Mapping(target = "phoneNumber", source = "userInfo.phoneNumber")
    @Mapping(target = "dateOfBirth", source = "userInfo.dateOfBirth")
    @Mapping(target = "avatarUrl", source = "userInfo.avatarUrl")
    @Mapping(target = "roles", expression = "java(getRoleNames(userEntity.getId()))")
    public abstract UserResponse entityToResponse(UserEntity userEntity);
    
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "dateOfBirth", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "roles", ignore = true)
    public abstract UserResponse toResponse(User user);
    
    protected List<String> getRoleNames(Long userId) {
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        return roleIds.stream()
                .map(roleId -> roleRepository.findById(roleId))
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get().getName())
                .toList();
    }
}
