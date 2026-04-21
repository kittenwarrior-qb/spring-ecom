package com.example.spring_ecom.config;

import lombok.Getter;
import lombok.Setter;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;

import java.util.Objects;

@Configuration
@ConfigurationProperties(prefix = "mqtt")
@Getter
@Setter
public class MqttPublisherConfig {

    private String brokerUrl = "tcp://localhost:1883";
    private String websocketUrl = "ws://localhost:8083/mqtt";
    private String clientId = "spring-ecom-landingpage";
    private String username;
    private String password;
    private int completionTimeout = 5000;
    private int connectionTimeout = 30;
    private boolean cleanSession = true;
    private boolean autoReconnect = true;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { brokerUrl });
        if (Objects.nonNull(username) && !username.isBlank()) {
            options.setUserName(username);
        }
        if (Objects.nonNull(password) && !password.isBlank()) {
            options.setPassword(password.toCharArray());
        }
        options.setCleanSession(cleanSession);
        options.setAutomaticReconnect(autoReconnect);
        options.setConnectionTimeout(connectionTimeout);
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MqttPahoMessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(
                clientId + "-publisher", 
                mqttClientFactory()
        );
        messageHandler.setAsync(true);
        messageHandler.setDefaultQos(1);
        messageHandler.setDefaultRetained(false);
        messageHandler.setCompletionTimeout(completionTimeout);
        return messageHandler;
    }
}
