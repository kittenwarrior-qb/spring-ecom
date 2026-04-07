package com.example.spring_ecom.repository.database.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    Page<NotificationEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<NotificationEntity> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    // Broadcast notifications (userId = null)
    Page<NotificationEntity> findByUserIdIsNullOrderByCreatedAtDesc(Pageable pageable);

    List<NotificationEntity> findByUserIdIsNullAndIsReadFalseOrderByCreatedAtDesc();

    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.userId = :userId AND n.isRead = false")
    long countUnreadByUserId(Long userId);

    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.userId IS NULL AND n.isRead = false")
    long countUnreadBroadcast();

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.userId = :userId AND n.id IN :ids")
    int markAsRead(Long userId, List<Long> ids);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
    int markAllAsRead(Long userId);

    void deleteByUserId(Long userId);
}
