package com.example.spring_ecom.service.notification;

import com.example.spring_ecom.domain.notification.Notification;
import com.example.spring_ecom.repository.database.notification.NotificationRepository;
import com.example.spring_ecom.repository.database.notification.NotificationUserReadEntity;
import com.example.spring_ecom.repository.database.notification.NotificationUserReadRepository;
import com.example.spring_ecom.repository.grpc.notification.NotificationGrpcClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCommandService {

    private final NotificationRepository notificationRepository;
    private final NotificationUserReadRepository notificationUserReadRepository;
    private final NotificationGrpcClient grpcClient;

    @Transactional
    public Notification createAndSend(Notification notification) {
        try {
            grpcClient.sendToUser(notification);
            log.info("[NOTIFICATION] Sent to user: userId={}, type={}", 
                    notification.userId(), notification.type());
            return notification;
        } catch (Exception e) {
            log.error("[NOTIFICATION] Failed to send: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void broadcast(Notification notification) {
        try {
            grpcClient.broadcast(notification);
            log.info("[NOTIFICATION] Broadcast: type={}", notification.type());
        } catch (Exception e) {
            log.error("[NOTIFICATION] Failed to broadcast: {}", e.getMessage());
            throw e;
        }
    }

    // ========== Query Operations (still use local DB) ==========

    @Transactional
    public void markAsRead(Long userId, List<Long> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return;
        }

        notificationRepository.markAsRead(userId, notificationIds);

        List<Long> broadcastIds = notificationRepository.findBroadcastIdsByIds(notificationIds);
        if (broadcastIds.isEmpty()) {
            return;
        }

        List<Long> alreadyRead = notificationUserReadRepository.findReadNotificationIds(userId, broadcastIds);
        Set<Long> alreadyReadSet = new HashSet<>(alreadyRead);

        List<NotificationUserReadEntity> newReadMarkers = new ArrayList<>();
        for (Long notificationId : broadcastIds) {
            if (!alreadyReadSet.contains(notificationId)) {
                newReadMarkers.add(NotificationUserReadEntity.builder()
                        .notificationId(notificationId)
                        .userId(userId)
                        .build());
            }
        }

        if (!newReadMarkers.isEmpty()) {
            notificationUserReadRepository.saveAll(newReadMarkers);
        }
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);

        List<Long> unreadBroadcastIds = notificationRepository.findUnreadBroadcastIdsForUser(userId);
        if (unreadBroadcastIds.isEmpty()) {
            return;
        }

        List<NotificationUserReadEntity> newReadMarkers = unreadBroadcastIds.stream()
                .map(notificationId -> NotificationUserReadEntity.builder()
                        .notificationId(notificationId)
                        .userId(userId)
                        .build())
                .toList();
        notificationUserReadRepository.saveAll(newReadMarkers);
    }

    // ========== Helper Methods ==========

    public Notification sendOrderStatusNotification(Long userId, Long orderId,
            String orderNumber, String status, String imageUrl) {
        log.info("[NOTIFICATION-CORE] Creating order status notification: userId={}, orderId={}, status={}",
                userId, orderId, status);

        String title = "Cập nhật đơn hàng";
        String message = String.format("Đơn hàng #%s đã được %s", orderNumber, mapStatusToVietnamese(status));
        String actionUrl = "/orders/" + orderId;

        Notification notification = new Notification(
                null, userId, mapOrderStatusToType(status),
                title, message, orderId, "ORDER",
                imageUrl, actionUrl, false, null);

        return createAndSend(notification);
    }

    private String mapOrderStatusToType(String status) {
        return switch (status.toUpperCase()) {
            case "CONFIRMED" -> "ORDER_CONFIRMED";
            case "PROCESSING" -> "ORDER_STATUS";
            case "SHIPPED" -> "ORDER_SHIPPED";
            case "DELIVERED" -> "ORDER_DELIVERED";
            case "CANCELLED" -> "ORDER_CANCELLED";
            default -> "ORDER_STATUS";
        };
    }

    private String mapStatusToVietnamese(String status) {
        return switch (status.toUpperCase()) {
            case "CONFIRMED" -> "xác nhận";
            case "PROCESSING" -> "đang xử lý";
            case "SHIPPED" -> "giao cho đơn vị vận chuyển";
            case "DELIVERED" -> "giao thành công";
            case "CANCELLED" -> "hủy";
            default -> status.toLowerCase();
        };
    }
}
