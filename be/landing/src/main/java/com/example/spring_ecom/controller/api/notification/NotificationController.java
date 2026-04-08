package com.example.spring_ecom.controller.api.notification;

import com.example.spring_ecom.controller.api.notification.model.NotificationRequest;
import com.example.spring_ecom.controller.api.notification.model.NotificationRequestMapper;
import com.example.spring_ecom.controller.api.notification.model.NotificationResponse;
import com.example.spring_ecom.controller.api.notification.model.NotificationResponseMapper;
import com.example.spring_ecom.emqx.domain.NotificationEvent;
import com.example.spring_ecom.service.notification.NotificationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Notification Controller
 * Only calls UseCase and mappers - no business logic
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {

    private final NotificationUseCase notificationUseCase;
    private final NotificationRequestMapper requestMapper;
    private final NotificationResponseMapper responseMapper;

    @Override
    public ResponseEntity<NotificationResponse> sendNotification(NotificationRequest request) {
        log.info("[NOTIFICATION-CTRL] Send notification request - userId={}, type={}, title={}",
                request.userId(), request.type(), request.title());
        
        NotificationEvent event = requestMapper.toEvent(request);
        notificationUseCase.sendToUser(event);
        
        return ResponseEntity.ok(responseMapper.toResponse(event));
    }

    @Override
    public ResponseEntity<NotificationResponse> broadcastNotification(NotificationRequest request) {
        log.info("[NOTIFICATION-CTRL] Broadcast notification request - type={}, title={}, message={}",
                request.type(), request.title(), request.message());
        
        NotificationEvent event = requestMapper.toEvent(request);
        notificationUseCase.broadcast(event);
        
        return ResponseEntity.ok(responseMapper.toResponse(event));
    }
}
