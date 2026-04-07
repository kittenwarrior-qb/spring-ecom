package com.example.spring_ecom.service.notification;

import com.example.spring_ecom.domain.notification.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationUseCaseService implements NotificationUseCase {

    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;

    // ========== Command Methods ==========

    @Override
    @Transactional
    public Notification createAndSend(Long userId, String type, String title, String message,
                                       Long referenceId, String referenceType, String imageUrl, String actionUrl) {
        Notification notification = new Notification(
                null, userId, type, title, message,
                referenceId, referenceType, imageUrl, actionUrl,
                false, null
        );
        return notificationCommandService.createAndSend(notification);
    }

    @Override
    @Transactional
    public void markAsRead(Long userId, List<Long> notificationIds) {
        notificationCommandService.markAsRead(userId, notificationIds);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationCommandService.markAllAsRead(userId);
    }

    // ========== Query Methods ==========

    @Override
    @Transactional(readOnly = true)
    public Optional<Notification> findById(Long id) {
        return notificationQueryService.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> findByUserId(Long userId, Pageable pageable) {
        return notificationQueryService.findByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findUnreadByUserId(Long userId) {
        return notificationQueryService.findUnreadByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadByUserId(Long userId) {
        return notificationQueryService.countUnreadByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> findUserAndBroadcastNotifications(Long userId, Pageable pageable) {
        return notificationQueryService.findUserAndBroadcastNotifications(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findUnreadUserAndBroadcastNotifications(Long userId) {
        return notificationQueryService.findUnreadUserAndBroadcastNotifications(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadUserAndBroadcastNotifications(Long userId) {
        return notificationQueryService.countUnreadUserAndBroadcastNotifications(userId);
    }
}
