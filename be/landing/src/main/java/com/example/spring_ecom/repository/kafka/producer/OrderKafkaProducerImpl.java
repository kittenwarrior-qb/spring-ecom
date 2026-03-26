package com.example.spring_ecom.repository.kafka.producer;

import com.example.spring_ecom.kafka.domain.OrderEvent;
import com.example.spring_ecom.kafka.service.OrderKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderKafkaProducerImpl implements OrderKafkaProducer {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void send(OrderEvent event) {
        log.info("Sending OrderEvent to Kafka topic [{}]: OrderId={}, Type={}", 
                 TOPIC, event.getOrderId(), event.getEventType());
        
        try {
            // Send the event to the topic defined in the interface
            kafkaTemplate.send(TOPIC, String.valueOf(event.getOrderId()), event);
            log.info("Successfully sent OrderEvent to Kafka");
        } catch (Exception e) {
            log.error("Failed to send OrderEvent to Kafka: {}", e.getMessage(), e);
        }
    }
}
