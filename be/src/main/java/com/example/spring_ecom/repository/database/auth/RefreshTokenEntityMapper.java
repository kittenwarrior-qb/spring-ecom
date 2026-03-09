package com.example.spring_ecom.repository.database.auth;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.auth.RefreshToken;
import org.mapstruct.Mapper;

@Mapper(config = MapStructGlobalConfig.class)
public interface RefreshTokenEntityMapper extends BaseEntityMapper<RefreshToken, RefreshTokenEntity> {
}
