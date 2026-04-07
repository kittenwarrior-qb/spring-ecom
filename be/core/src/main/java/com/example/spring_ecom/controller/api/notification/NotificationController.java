package com.example.spring_ecom.controller.api.notification;

import com.example.spring_ecom.controller.api.notification.model.NotificationResponse;
import com.example.spring_ecom.controller.api.notification.model.NotificationResponseMapper;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.SecurityUtil;
import com.example.spring_ecom.domain.notification.Notification;
import com.example.spring_ecom.service.notification.NotificationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/api/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationAPI {

    private final NotificationUseCase notificationUseCase;
    private final NotificationResponseMapper responseMapper;

    @Override
    public ApiResponse<Page<NotificationResponse>> getNotifications(int page, int size) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ApiResponse.Error.of(ResponseCode.UNAUTHORIZED, "User not authenticated");
        }
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        // Get both user notifications and broadcast notifications
        Page<Notification> notifications = notificationUseCase.findUserAndBroadcastNotifications(userId, pageable);
        Page<NotificationResponse> response = notifications.map(responseMapper::toResponse);
        
        return ApiResponse.Success.of(ResponseCode.OK, "Notifications retrieved", response);
    }

    @Override
    public ApiResponse<List<NotificationResponse>> getUnreadNotifications() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ApiResponse.Error.of(ResponseCode.UNAUTHORIZED, "User not authenticated");
        }
        
        // Get both unread user notifications and unread broadcast notifications
        List<Notification> notifications = notificationUseCase.findUnreadUserAndBroadcastNotifications(userId);
        List<NotificationResponse> response = notifications.stream()
                .map(responseMapper::toResponse)
                .toList();
        
        return ApiResponse.Success.of(ResponseCode.OK, "Unread notifications retrieved", response);
    }

    @Override
    public ApiResponse<Long> getUnreadCount() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ApiResponse.Error.of(ResponseCode.UNAUTHORIZED, "User not authenticated");
        }
        // Count both unread user notifications and unread broadcast notifications
        long count = notificationUseCase.countUnreadUserAndBroadcastNotifications(userId);
        
        return ApiResponse.Success.of(ResponseCode.OK, "Unread count retrieved", count);
    }

    @Override
    public ApiResponse<Void> markAsRead(List<Long> notificationIds) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ApiResponse.Error.of(ResponseCode.UNAUTHORIZED, "User not authenticated");
        }
        notificationUseCase.markAsRead(userId, notificationIds);
        
        return ApiResponse.Success.of(ResponseCode.OK, "Notifications marked as read", null);
    }

    @Override
    public ApiResponse<Void> markAllAsRead() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ApiResponse.Error.of(ResponseCode.UNAUTHORIZED, "User not authenticated");
        }
        notificationUseCase.markAllAsRead(userId);
        
        return ApiResponse.Success.of(ResponseCode.OK, "All notifications marked as read", null);
    }

    private Long getCurrentUserId() {
        return SecurityUtil.getCurrentUserId();
    }
}
