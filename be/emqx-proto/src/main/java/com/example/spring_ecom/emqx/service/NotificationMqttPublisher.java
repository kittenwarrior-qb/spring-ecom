package com.example.spring_ecom.emqx.service;

import com.example.spring_ecom.emqx.domain.NotificationEvent;

public interface NotificationMqttPublisher {

    String NOTIFICATION_TOPIC_PREFIX = "notifications/";

    void publishToUser(NotificationEvent event);

    void publish(String topic, NotificationEvent event);

    void publish(String topic, NotificationEvent event, int qos);

    void broadcast(NotificationEvent event);
}
