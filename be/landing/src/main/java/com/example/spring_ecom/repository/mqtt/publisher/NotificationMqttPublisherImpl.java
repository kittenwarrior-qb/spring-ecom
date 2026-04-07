package com.example.spring_ecom.repository.mqtt.publisher;

import com.example.spring_ecom.emqx.domain.NotificationEvent;
import com.example.spring_ecom.emqx.service.NotificationMqttPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * MQTT Publisher for notification events
 * Located in landingpage module - acts as publisher
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationMqttPublisherImpl implements NotificationMqttPublisher {

    private final MqttPahoMessageHandler mqttOutbound;
    private final ObjectMapper objectMapper;

    @Override
    public void publishToUser(NotificationEvent event) {
        String topic = topicForUser(event.getUserId(), event.getType());
        publish(topic, event);
    }

    @Override
    public void publish(String topic, NotificationEvent event) {
        publish(topic, event, 1); // Default QoS 1
    }

    @Override
    public void publish(String topic, NotificationEvent event, int qos) {
        try {
            if (Objects.isNull(event.getEventId())) {
                event.setEventId(UUID.randomUUID().toString());
            }
            if (Objects.isNull(event.getTimestamp())) {
                event.setTimestamp(Instant.now());
            }
            if (Objects.isNull(event.getSource())) {
                event.setSource("landingpage");
            }

            String jsonPayload = objectMapper.writeValueAsString(event);
            Message<String> message = MessageBuilder
                    .withPayload(jsonPayload)
                    .setHeader("mqtt_topic", topic)
                    .setHeader("mqtt_qos", qos)
                    .build();

            mqttOutbound.handleMessage(message);
            log.info("[MQTT] Published: topic={}", topic);
        } catch (JsonProcessingException e) {
            log.error("[MQTT] Failed to serialize: {}", e.getMessage());
            throw new RuntimeException("Failed to serialize", e);
        }
    }

    @Override
    public void broadcast(NotificationEvent event) {
        String topic = topicForBroadcast(event.getType());
        publish(topic, event, 1);
    }

    private String topicForUser(Long userId, String type) {
        return String.format("%s%d/%s", NOTIFICATION_TOPIC_PREFIX, userId, type.toLowerCase());
    }

    private String topicForBroadcast(String type) {
        return String.format("%sbroadcast/%s", NOTIFICATION_TOPIC_PREFIX, type.toLowerCase());
    }
}
