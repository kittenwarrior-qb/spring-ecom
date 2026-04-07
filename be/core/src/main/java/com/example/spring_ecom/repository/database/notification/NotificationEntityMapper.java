package com.example.spring_ecom.repository.database.notification;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.core.mapper.BaseEntityMapper;
import com.example.spring_ecom.domain.notification.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructGlobalConfig.class)
public interface NotificationEntityMapper extends BaseEntityMapper<Notification, NotificationEntity> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isRead", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    NotificationEntity toEntity(Notification domain);

    @Override
    Notification toDomain(NotificationEntity entity);
}
