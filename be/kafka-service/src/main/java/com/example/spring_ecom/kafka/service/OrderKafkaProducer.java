package com.example.spring_ecom.kafka.service;

import com.example.spring_ecom.kafka.domain.OrderEvent;

public interface OrderKafkaProducer {
    String TOPIC = "order-events";
    
    void send(OrderEvent event);
}
