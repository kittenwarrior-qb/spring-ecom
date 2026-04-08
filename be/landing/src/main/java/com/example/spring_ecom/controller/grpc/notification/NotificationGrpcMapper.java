package com.example.spring_ecom.controller.grpc.notification;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.emqx.domain.NotificationEvent;
import com.example.spring_ecom.grpc.services.NotificationServiceProto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mapper for Notification gRPC Server
 * Maps between gRPC proto and domain event
 */
@Mapper(config = MapStructGlobalConfig.class)
public interface NotificationGrpcMapper {

    // gRPC Request -> Event
    @Mapping(target = "eventId", expression = "java(generateEventId())")
    @Mapping(target = "eventType", source = "type")
    @Mapping(target = "timestamp", expression = "java(now())")
    @Mapping(target = "source", constant = "grpc-core")
    @Mapping(target = "notificationId", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "referenceId", source = "referenceId")
    @Mapping(target = "referenceType", source = "referenceType")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "actionUrl", source = "actionUrl")
    @Mapping(target = "isRead", constant = "false")
    @Mapping(target = "createdAt", expression = "java(localNow())")
    NotificationEvent toEvent(NotificationServiceProto.SendNotificationRequest request);

    @Mapping(target = "eventId", expression = "java(generateEventId())")
    @Mapping(target = "eventType", source = "type")
    @Mapping(target = "timestamp", expression = "java(now())")
    @Mapping(target = "source", constant = "grpc-core")
    @Mapping(target = "notificationId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "type", source = "type")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "referenceId", source = "referenceId")
    @Mapping(target = "referenceType", source = "referenceType")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "actionUrl", source = "actionUrl")
    @Mapping(target = "isRead", constant = "false")
    @Mapping(target = "createdAt", expression = "java(localNow())")
    NotificationEvent toEvent(NotificationServiceProto.BroadcastNotificationRequest request);

    // Event -> gRPC Response
    default NotificationServiceProto.SendNotificationResponse toSendResponse(boolean success, String message, String eventId) {
        return NotificationServiceProto.SendNotificationResponse.newBuilder()
                .setSuccess(success)
                .setMessage(message)
                .setEventId(eventId != null ? eventId : "")
                .build();
    }

    default NotificationServiceProto.BroadcastNotificationResponse toBroadcastResponse(boolean success, String message, String eventId) {
        return NotificationServiceProto.BroadcastNotificationResponse.newBuilder()
                .setSuccess(success)
                .setMessage(message)
                .setEventId(eventId != null ? eventId : "")
                .build();
    }

    // Helper methods
    default String generateEventId() {
        return UUID.randomUUID().toString();
    }

    default Instant now() {
        return Instant.now();
    }

    default LocalDateTime localNow() {
        return LocalDateTime.now();
    }
}
