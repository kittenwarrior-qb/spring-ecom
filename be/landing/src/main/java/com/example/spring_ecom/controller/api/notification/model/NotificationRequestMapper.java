package com.example.spring_ecom.controller.api.notification.model;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.emqx.domain.NotificationEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Mapper for Notification Request -> Event
 */
@Mapper(config = MapStructGlobalConfig.class)
public interface NotificationRequestMapper {

    @Mapping(target = "eventId", expression = "java(generateEventId())")
    @Mapping(target = "eventType", source = "type")
    @Mapping(target = "timestamp", expression = "java(now())")
    @Mapping(target = "source", constant = "landingpage-api")
    @Mapping(target = "notificationId", ignore = true)
    @Mapping(target = "isRead", constant = "false")
    @Mapping(target = "createdAt", expression = "java(localNow())")
    NotificationEvent toEvent(NotificationRequest request);

    @Named("generateEventId")
    default String generateEventId() {
        return UUID.randomUUID().toString();
    }

    @Named("now")
    default Instant now() {
        return Instant.now();
    }

    @Named("localNow")
    default LocalDateTime localNow() {
        return LocalDateTime.now();
    }
}
