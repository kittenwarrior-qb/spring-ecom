package com.example.spring_ecom.repository.grpc.notification;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.domain.notification.Notification;
import com.example.spring_ecom.grpc.services.NotificationServiceProto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Objects;

/**
 * Mapper for Notification gRPC
 * Maps between domain DTO and gRPC proto
 */
@Mapper(config = MapStructGlobalConfig.class)
public interface NotificationGrpcMapper {

    // Domain -> gRPC Request
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "referenceId", source = "referenceId", defaultValue = "0L")
    @Mapping(target = "referenceType", source = "referenceType", defaultValue = "")
    @Mapping(target = "imageUrl", source = "imageUrl", qualifiedByName = "nullToEmpty")
    @Mapping(target = "actionUrl", source = "actionUrl", qualifiedByName = "nullToEmpty")
    NotificationServiceProto.SendNotificationRequest toSendRequest(Notification notification);

    @Mapping(target = "type", source = "type")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "referenceId", source = "referenceId", defaultValue = "0L")
    @Mapping(target = "referenceType", source = "referenceType", defaultValue = "")
    @Mapping(target = "imageUrl", source = "imageUrl", qualifiedByName = "nullToEmpty")
    @Mapping(target = "actionUrl", source = "actionUrl", qualifiedByName = "nullToEmpty")
    NotificationServiceProto.BroadcastNotificationRequest toBroadcastRequest(Notification notification);

    // gRPC Response -> Domain
    default boolean isSuccess(NotificationServiceProto.SendNotificationResponse response) {
        return response.getSuccess();
    }

    default String getEventId(NotificationServiceProto.SendNotificationResponse response) {
        return response.getEventId();
    }
    
    // Helper to convert null to empty string for proto fields
    @Named("nullToEmpty")
    default String nullToEmpty(String value) {
        return Objects.isNull(value) ? "" : value;
    }
}
