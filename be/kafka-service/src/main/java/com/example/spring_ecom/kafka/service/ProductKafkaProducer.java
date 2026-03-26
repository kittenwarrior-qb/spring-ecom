package com.example.spring_ecom.kafka.service;

import com.example.spring_ecom.kafka.domain.ProductEvent;

public interface ProductKafkaProducer {

    String TOPIC = "product-events";

    void send(ProductEvent event);
}
