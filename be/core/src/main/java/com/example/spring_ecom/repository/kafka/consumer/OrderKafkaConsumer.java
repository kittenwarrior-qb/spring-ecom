package com.example.spring_ecom.repository.kafka.consumer;

import com.example.spring_ecom.kafka.domain.OrderEvent;
import com.example.spring_ecom.kafka.service.OrderKafkaProducer;
import com.example.spring_ecom.repository.kafka.service.OrderEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaConsumer {

    private final OrderEventService orderEventService;

    @KafkaListener(topics = OrderKafkaProducer.TOPIC, groupId = "spring-ecom-server-group")
    public void consumeOrderEvent(OrderEvent event) {
        try {
            switch (event.getEventType()) {
                case OrderEvent.CREATED -> {
                    log.info("Processing ORDER_CREATED event...");
                    orderEventService.handleOrderCreated(event);
                }
                case "ORDER_PAID" -> {
                    log.info("Processing ORDER_PAID event...");
                    orderEventService.handleOrderPaid(event);
                }
                case OrderEvent.CANCELLED -> {
                    log.info("Processing ORDER_CANCELLED event...");
                    orderEventService.handleOrderCancelled(event);
                }
                case OrderEvent.DELIVERED -> {
                    log.info("Processing ORDER_DELIVERED event...");
                    orderEventService.handleOrderDelivered(event);
                }
                case OrderEvent.STATUS_CHANGED -> {
                    log.info("Processing ORDER_STATUS_CHANGED event...");
                    log.info("Status: {} -> {}", event.getPreviousStatus(), event.getStatus());
                    orderEventService.handleOrderStatusChanged(event);
                }
                case "ORDER_PARTIAL_CANCELLED" -> {
                    log.info("Processing ORDER_PARTIAL_CANCELLED event...");
                    orderEventService.handleOrderPartialCancelled(event);
                }
                default -> {
                    log.warn("Unknown event type: {}", event.getEventType());
                }
            }
            log.info("Event processed successfully: {}", event.getEventId());
        } catch (RuntimeException e) {
            log.error("Failed to process event: {}, error: {}", event.getEventId(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to process event: {}, error: {}", event.getEventId(), e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
