package com.example.spring_ecom.controller.grpc.notification;

import com.example.spring_ecom.emqx.domain.NotificationEvent;
import com.example.spring_ecom.grpc.services.NotificationServiceGrpc;
import com.example.spring_ecom.grpc.services.NotificationServiceProto;
import com.example.spring_ecom.service.notification.NotificationUseCase;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class NotificationGrpcService extends NotificationServiceGrpc.NotificationServiceImplBase {

    private final NotificationUseCase notificationUseCase;
    private final NotificationGrpcMapper mapper;

    @Override
    public void sendNotification(NotificationServiceProto.SendNotificationRequest request,
                                 StreamObserver<NotificationServiceProto.SendNotificationResponse> responseObserver) {
        try {
            NotificationEvent event = mapper.toEvent(request);
            notificationUseCase.sendToUser(event);
            log.info("[GRPC] Notification sent: userId={}, type={}, title={}, eventId={}",
                    event.getUserId(), event.getType(), event.getTitle(), event.getEventId());
            responseObserver.onNext(mapper.toSendResponse(true, "OK", event.getEventId()));
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[GRPC] Failed: {}", e.getMessage());
            responseObserver.onNext(mapper.toSendResponse(false, e.getMessage(), null));
            responseObserver.onCompleted();
        }
    }

    @Override
    public void broadcastNotification(NotificationServiceProto.BroadcastNotificationRequest request,
                                       StreamObserver<NotificationServiceProto.BroadcastNotificationResponse> responseObserver) {
        try {
            NotificationEvent event = mapper.toEvent(request);
            notificationUseCase.broadcast(event);
            log.info("[GRPC] Broadcast sent: type={}, title={}, eventId={}",
                    event.getType(), event.getTitle(), event.getEventId());
            responseObserver.onNext(mapper.toBroadcastResponse(true, "OK", event.getEventId()));
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[GRPC] Failed: {}", e.getMessage());
            responseObserver.onNext(mapper.toBroadcastResponse(false, e.getMessage(), null));
            responseObserver.onCompleted();
        }
    }
}
