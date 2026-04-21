package com.example.spring_ecom.kafka.service;

import com.example.spring_ecom.kafka.domain.UserEvent;

public interface UserKafkaProducer {
    String TOPIC = "user-events";
    
    void send(UserEvent event);
}
