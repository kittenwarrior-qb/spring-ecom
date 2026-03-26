package com.example.spring_ecom.repository.kafka.producer;

import com.example.spring_ecom.kafka.domain.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka producer for Order events from Server service
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaProducerImpl {
    
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    
    private static final String ORDER_EVENTS_TOPIC = "order-events";
    
    public void send(OrderEvent event) {
        try {
            kafkaTemplate.send(ORDER_EVENTS_TOPIC, event.getOrderId().toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to send order event: eventId={}, type={}", 
                                    event.getEventId(), event.getEventType(), ex);
                        } else {
                            log.debug("Sent order event: eventId={}, type={}, partition={}", 
                                    event.getEventId(), event.getEventType(), 
                                    result.getRecordMetadata().partition());
                        }
                    });
        } catch (Exception e) {
            log.error("Error sending order event: eventId={}, type={}", 
                    event.getEventId(), event.getEventType(), e);
        }
    }
}
