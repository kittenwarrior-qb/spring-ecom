package com.example.spring_ecom.controller.api.notification;

import com.example.spring_ecom.controller.api.notification.model.NotificationRequest;
import com.example.spring_ecom.controller.api.notification.model.NotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Notification API Interface
 * Defines endpoints for MQTT notifications
 */
@RequestMapping("/v1/api/notifications")
@Tag(name = "Notifications (MQTT)", description = "Real-time notifications via MQTT")
public interface NotificationApi {

    @PostMapping("/send")
    @Operation(summary = "Send notification via MQTT",
               description = "Publishes notification to EMQX broker for real-time delivery")
    ResponseEntity<NotificationResponse> sendNotification(
            @Valid @RequestBody NotificationRequest request);

    @PostMapping("/broadcast")
    @Operation(summary = "Broadcast notification to all users",
               description = "Publishes broadcast notification to all subscribed clients")
    ResponseEntity<NotificationResponse> broadcastNotification(
            @Valid @RequestBody NotificationRequest request);
}
