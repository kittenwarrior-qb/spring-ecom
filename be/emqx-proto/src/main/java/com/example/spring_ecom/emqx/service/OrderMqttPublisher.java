package com.example.spring_ecom.emqx.service;

import com.example.spring_ecom.emqx.domain.OrderNotificationEvent;

/**
 * Interface for publishing order-related events via MQTT
 * Used for real-time order status updates to clients
 */
public interface OrderMqttPublisher {

    String ORDER_TOPIC_PREFIX = "orders/";

    /**
     * Publish order status change event
     * Topic: orders/{userId}/status
     */
    void publishStatusChange(OrderNotificationEvent event);

    /**
     * Publish order created event
     * Topic: orders/{userId}/created
     */
    void publishOrderCreated(OrderNotificationEvent event);

    /**
     * Publish order cancelled event
     * Topic: orders/{userId}/cancelled
     */
    void publishOrderCancelled(OrderNotificationEvent event);

    /**
     * Publish order delivered event
     * Topic: orders/{userId}/delivered
     */
    void publishOrderDelivered(OrderNotificationEvent event);

    /**
     * Publish to custom topic
     */
    void publish(String topic, OrderNotificationEvent event);
}
