package com.example.spring_ecom.service.notification;

import com.example.spring_ecom.domain.notification.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NotificationUseCase {

    // Command methods
    Notification createAndSend(Long userId, String type, String title, String message,
                               Long referenceId, String referenceType, String imageUrl, String actionUrl);

    void markAsRead(Long userId, List<Long> notificationIds);

    void markAllAsRead(Long userId);

    // Query methods
    Optional<Notification> findById(Long id);

    Page<Notification> findByUserId(Long userId, Pageable pageable);

    // Get both user notifications and broadcast notifications
    Page<Notification> findUserAndBroadcastNotifications(Long userId, Pageable pageable);

    List<Notification> findUnreadByUserId(Long userId);

    // Get both unread user notifications and unread broadcast notifications
    List<Notification> findUnreadUserAndBroadcastNotifications(Long userId);

    long countUnreadByUserId(Long userId);

    // Count both unread user notifications and unread broadcast notifications
    long countUnreadUserAndBroadcastNotifications(Long userId);
}
