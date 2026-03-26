package com.example.spring_ecom.repository.database.userInfo;

import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.userInfo.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserInfoEntityMapper extends BaseEntityMapper<UserInfo, UserInfoEntity> {
    
    @Override
    @Mapping(target = "user", ignore = true)
    UserInfoEntity toEntity(UserInfo domain);
    
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void update(@MappingTarget UserInfoEntity entity, UserInfo domain);
}