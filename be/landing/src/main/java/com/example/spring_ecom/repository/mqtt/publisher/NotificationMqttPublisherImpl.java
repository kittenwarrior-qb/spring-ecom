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

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationMqttPublisherImpl implements NotificationMqttPublisher {

    private final MqttPahoMessageHandler mqttOutbound;
    private final ObjectMapper objectMapper;

    @Override
    public void publishToUser(NotificationEvent event) {
        String topic = String.format("%s%d/%s", NOTIFICATION_TOPIC_PREFIX, event.getUserId(), event.getType().toLowerCase());
        publish(topic, event);
        log.info("[MQTT] Published to user: topic={}, userId={}, type={}, eventId={}",
                topic, event.getUserId(), event.getType(), event.getEventId());
    }

    @Override
    public void broadcast(NotificationEvent event) {
        String topic = String.format("%sbroadcast/%s", NOTIFICATION_TOPIC_PREFIX, event.getType().toLowerCase());
        publish(topic, event);
        log.info("[MQTT] Broadcast: topic={}, type={}, title={}, eventId={}",
                topic, event.getType(), event.getTitle(), event.getEventId());
    }

    private void publish(String topic, NotificationEvent event) {
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
                        .setHeader("mqtt_qos", 1)
                        .build();

            mqttOutbound.handleMessage(message);
        } catch (JsonProcessingException e) {
            log.error("[MQTT] Failed to serialize: {}", e.getMessage());
            throw new RuntimeException("Failed to serialize", e);
        }
    }
}
