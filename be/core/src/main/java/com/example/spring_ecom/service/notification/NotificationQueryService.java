package com.example.spring_ecom.service.notification;

import com.example.spring_ecom.domain.notification.Notification;
import com.example.spring_ecom.repository.database.notification.NotificationEntity;
import com.example.spring_ecom.repository.database.notification.NotificationEntityMapper;
import com.example.spring_ecom.repository.database.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;
    private final NotificationEntityMapper mapper;

    public Optional<Notification> findById(Long id) {
        return notificationRepository.findById(id)
                .map(mapper::toDomain);
    }

    public Page<Notification> findByUserId(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(mapper::toDomain);
    }

    // Get both user notifications and broadcast notifications
    public Page<Notification> findUserAndBroadcastNotifications(Long userId, Pageable pageable) {
        // Get user notifications
        Page<NotificationEntity> userNotifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        // Get broadcast notifications
        Page<NotificationEntity> broadcastNotifications = notificationRepository.findByUserIdIsNullOrderByCreatedAtDesc(pageable);
        
        // Combine both lists
        List<Notification> combined = new ArrayList<>();
        combined.addAll(userNotifications.getContent().stream().map(mapper::toDomain).toList());
        combined.addAll(broadcastNotifications.getContent().stream().map(mapper::toDomain).toList());
        
        // Sort by created date descending
        combined.sort((a, b) -> b.createdAt().compareTo(a.createdAt()));
        
        // Create a new page with combined results
        int start = Math.min((int) pageable.getOffset(), combined.size());
        int end = Math.min((start + pageable.getPageSize()), combined.size());
        List<Notification> pageContent = combined.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, combined.size());
    }

    public List<Notification> findUnreadByUserId(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    // Get both unread user notifications and unread broadcast notifications
    public List<Notification> findUnreadUserAndBroadcastNotifications(Long userId) {
        // Get unread user notifications
        List<Notification> userUnread = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
        
        // Get unread broadcast notifications
        List<Notification> broadcastUnread = notificationRepository.findByUserIdIsNullAndIsReadFalseOrderByCreatedAtDesc()
                .stream()
                .map(mapper::toDomain)
                .toList();
        
        // Combine and sort
        List<Notification> combined = new ArrayList<>();
        combined.addAll(userUnread);
        combined.addAll(broadcastUnread);
        combined.sort((a, b) -> b.createdAt().compareTo(a.createdAt()));
        
        return combined;
    }

    public long countUnreadByUserId(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    // Count both unread user notifications and unread broadcast notifications
    public long countUnreadUserAndBroadcastNotifications(Long userId) {
        return notificationRepository.countUnreadByUserId(userId) + notificationRepository.countUnreadBroadcast();
    }
}
