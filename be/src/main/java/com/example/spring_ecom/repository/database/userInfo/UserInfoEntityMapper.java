package com.example.spring_ecom.repository.database.userInfo;

import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.userInfo.UserInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserInfoEntityMapper extends BaseEntityMapper<UserInfo, UserInfoEntity> {
}