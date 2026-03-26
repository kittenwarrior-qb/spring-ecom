package com.example.spring_ecom.repository.kafka.producer;

import com.example.spring_ecom.kafka.domain.UserEvent;
import com.example.spring_ecom.kafka.service.UserKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserKafkaProducerImpl implements UserKafkaProducer {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void send(UserEvent event) {
        log.info("Sending UserEvent to Kafka topic [{}]: UserId={}, Type={}", 
                 TOPIC, event.getUserId(), event.getEventType());
        
        try {
            kafkaTemplate.send(TOPIC, String.valueOf(event.getUserId()), event);
            log.info("Successfully sent UserEvent to Kafka");
        } catch (Exception e) {
            log.error("Failed to send UserEvent to Kafka: {}", e.getMessage(), e);
        }
    }
}
