package com.example.spring_ecom.service.notification;

import com.example.spring_ecom.emqx.domain.NotificationEvent;

public interface NotificationUseCase {

    void sendToUser(NotificationEvent event);

    void broadcast(NotificationEvent event);
}
