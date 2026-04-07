package com.example.spring_ecom.controller.api.notification;

import com.example.spring_ecom.controller.api.notification.model.NotificationResponse;
import com.example.spring_ecom.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notification", description = "Notification management API")
@RequestMapping("/v1/api/notifications")
public interface NotificationAPI {

    @Operation(summary = "Get user notifications")
    @GetMapping
    ApiResponse<Page<NotificationResponse>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    );

    @Operation(summary = "Get unread notifications")
    @GetMapping("/unread")
    ApiResponse<List<NotificationResponse>> getUnreadNotifications();

    @Operation(summary = "Get unread count")
    @GetMapping("/unread/count")
    ApiResponse<Long> getUnreadCount();

    @Operation(summary = "Mark notifications as read")
    @PutMapping("/read")
    ApiResponse<Void> markAsRead(@RequestBody List<Long> notificationIds);

    @Operation(summary = "Mark all notifications as read")
    @PutMapping("/read/all")
    ApiResponse<Void> markAllAsRead();
}
