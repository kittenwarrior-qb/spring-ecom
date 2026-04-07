package com.example.spring_ecom.service.notification;

import com.example.spring_ecom.emqx.domain.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Notification UseCase Service
 * Orchestrates notification operations
 */
@Service
@RequiredArgsConstructor
public class NotificationUseCaseService implements NotificationUseCase {

    private final NotificationCommandService notificationCommandService;

    @Override
    public void sendToUser(NotificationEvent event) {
        notificationCommandService.publishToUser(event);
    }

    @Override
    public void broadcast(NotificationEvent event) {
        notificationCommandService.broadcast(event);
    }
}
