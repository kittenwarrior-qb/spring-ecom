package com.example.spring_ecom.controller.api.notification.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.emqx.domain.NotificationEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for Notification Event -> Response
 */
@Mapper(config = MapStructGlobalConfig.class)
public interface NotificationResponseMapper {

    @Mapping(target = "success", constant = "true")
    @Mapping(target = "message", constant = "Notification sent successfully")
    @Mapping(target = "eventId", source = "eventId")
    NotificationResponse toResponse(NotificationEvent event);

    default NotificationResponse toErrorResponse(String errorMessage) {
        return new NotificationResponse(false, errorMessage, null);
    }
}
