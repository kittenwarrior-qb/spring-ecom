package com.example.spring_ecom.repository.database.notification;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.emqx.domain.NotificationEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface NotificationEntityMapper extends BaseEntityMapper<NotificationEvent, NotificationEntity> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isRead", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "referenceId", source = "referenceId")
    @Mapping(target = "referenceType", source = "referenceType")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "actionUrl", source = "actionUrl")
    NotificationEntity toEntity(NotificationEvent domain);

    @Override
    NotificationEvent toDomain(NotificationEntity entity);
}
