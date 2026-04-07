package com.example.spring_ecom.service.notification;

import com.example.spring_ecom.emqx.domain.NotificationEvent;
import com.example.spring_ecom.emqx.service.NotificationMqttPublisher;
import com.example.spring_ecom.repository.database.notification.NotificationEntity;
import com.example.spring_ecom.repository.database.notification.NotificationEntityMapper;
import com.example.spring_ecom.repository.database.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCommandService {

    private final NotificationRepository notificationRepository;
    private final NotificationEntityMapper mapper;
    private final NotificationMqttPublisher mqttPublisher;

    @Transactional
    public void publishToUser(NotificationEvent event) {
        validateEventFields(event);
        saveToDatabase(event);
        mqttPublisher.publishToUser(event);
        log.info("[NOTIFICATION] Published to user: userId={}, type={}", 
                event.getUserId(), event.getType());
    }

    @Transactional
    public void broadcast(NotificationEvent event) {
        validateEventFields(event);
        saveToDatabase(event);
        mqttPublisher.broadcast(event);
        log.info("[NOTIFICATION] Broadcast: type={}", event.getType());
    }

    private void saveToDatabase(NotificationEvent event) {
        NotificationEntity entity = mapper.toEntity(event);
        entity = notificationRepository.save(entity);
        event.setNotificationId(entity.getId());
    }

    private void validateEventFields(NotificationEvent event) {
        if (Objects.isNull(event.getEventId())) {
            event.setEventId(UUID.randomUUID().toString());
        }
        if (Objects.isNull(event.getTimestamp())) {
            event.setTimestamp(Instant.now());
        }
        if (Objects.isNull(event.getSource())) {
            event.setSource("landingpage");
        }
    }
}
