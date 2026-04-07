package com.example.spring_ecom.repository.grpc.notification;

import com.example.spring_ecom.domain.notification.Notification;
import com.example.spring_ecom.grpc.services.NotificationServiceGrpc;
import com.example.spring_ecom.grpc.services.NotificationServiceProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationGrpcClient {

    private final NotificationGrpcMapper mapper;

    @GrpcClient("landingpage-service")
    private NotificationServiceGrpc.NotificationServiceBlockingStub stub;

    /**
     * Send notification to specific user via gRPC -> MQTT
     */
    public void sendToUser(Notification notification) {
        if (Objects.isNull(notification) || Objects.isNull(notification.userId())) {
            log.warn("[GRPC] Invalid notification");
            return;
        }

        try {
            NotificationServiceProto.SendNotificationRequest request = mapper.toSendRequest(notification);
            NotificationServiceProto.SendNotificationResponse response = stub.sendNotification(request);

            if (!response.getSuccess()) {
                log.error("[GRPC] Failed: {}", response.getMessage());
            }
        } catch (Exception e) {
            log.error("[GRPC] Call failed: {}", e.getMessage());
        }
    }

    /**
     * Broadcast notification to all users via gRPC -> MQTT
     */
    public void broadcast(Notification notification) {
        if (Objects.isNull(notification)) {
            log.warn("[GRPC] Invalid notification");
            return;
        }

        try {
            NotificationServiceProto.BroadcastNotificationRequest request = mapper.toBroadcastRequest(notification);

            if (Objects.isNull(request)) {
                log.error("[GRPC] Failed to create request");
                return;
            }

            NotificationServiceProto.BroadcastNotificationResponse response = stub.broadcastNotification(request);

            if (!response.getSuccess()) {
                log.error("[GRPC] Failed: {}", response.getMessage());
            }
        } catch (Exception e) {
            log.error("[GRPC] Call failed: {}", e.getMessage());
        }
    }
}
