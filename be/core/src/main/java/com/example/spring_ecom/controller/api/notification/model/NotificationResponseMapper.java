package com.example.spring_ecom.controller.api.notification.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.config.MinioConfig;
import com.example.spring_ecom.domain.notification.Notification;
import com.example.spring_ecom.repository.database.notification.NotificationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Objects;

@Mapper(config = MapStructGlobalConfig.class)
public interface NotificationResponseMapper {

    NotificationResponse toResponse(Notification domain);

    NotificationResponse toResponse(NotificationEntity entity);

    @Named("toFullUrl")
    default String toFullUrl(String filename) {
        if (Objects.isNull(filename) || filename.isBlank()) {
            return null;
        }
        if (filename.startsWith("http://") || filename.startsWith("https://")) {
            return filename;
        }
        // This will be injected via Spring if needed
        return filename;
    }
}
