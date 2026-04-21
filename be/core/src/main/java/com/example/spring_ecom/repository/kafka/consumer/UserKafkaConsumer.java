package com.example.spring_ecom.repository.kafka.consumer;

import com.example.spring_ecom.kafka.domain.UserEvent;
import com.example.spring_ecom.kafka.service.UserKafkaProducer;
import com.example.spring_ecom.service.auth.email.EmailCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserKafkaConsumer {

    private final EmailCommandService emailCommandService;

    @KafkaListener(topics = UserKafkaProducer.TOPIC)
    public void consumeUserEvent(UserEvent event) {
        try {
            if (UserEvent.REGISTERED.equals(event.getEventType())) {
                log.info("Processing REGISTERED event: Sending verification email to {}", event.getEmail());
                emailCommandService.sendVerificationEmail(event.getUserId());
            } else {
                log.warn("Unknown user event type received: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Failed to process UserEvent: {}", e.getMessage(), e);
        }
    }
}
