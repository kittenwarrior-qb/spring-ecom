package com.example.spring_ecom.service.notification;

import com.example.spring_ecom.emqx.domain.NotificationEvent;

/**
 * Notification UseCase Interface
 * Defines all notification operations
 */
public interface NotificationUseCase {

    /**
     * Send notification to specific user via MQTT
     */
    void sendToUser(NotificationEvent event);

    /**
     * Broadcast notification to all users via MQTT
     */
    void broadcast(NotificationEvent event);
}
