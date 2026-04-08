package com.example.spring_ecom.emqx.service;

import com.example.spring_ecom.emqx.domain.NotificationEvent;

public interface NotificationMqttPublisher {

    String NOTIFICATION_TOPIC_PREFIX = "notifications/";

    void publishToUser(NotificationEvent event);

    void broadcast(NotificationEvent event);
}
