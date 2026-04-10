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

    @Query("""
        SELECT n FROM NotificationEntity n
        WHERE n.userId IS NULL
          AND NOT EXISTS (
            SELECT 1 FROM NotificationUserReadEntity r
            WHERE r.notificationId = n.id
              AND r.userId = :userId
          )
        ORDER BY n.createdAt DESC
    """)
    List<NotificationEntity> findUnreadBroadcastForUser(Long userId);

    @Query("""
        SELECT COUNT(n) FROM NotificationEntity n
        WHERE n.userId IS NULL
          AND NOT EXISTS (
            SELECT 1 FROM NotificationUserReadEntity r
            WHERE r.notificationId = n.id
              AND r.userId = :userId
          )
    """)
    long countUnreadBroadcastForUser(Long userId);

    @Query("SELECT n.id FROM NotificationEntity n WHERE n.userId IS NULL AND n.id IN :ids")
    List<Long> findBroadcastIdsByIds(List<Long> ids);

    @Query("""
        SELECT n.id FROM NotificationEntity n
        WHERE n.userId IS NULL
          AND NOT EXISTS (
            SELECT 1 FROM NotificationUserReadEntity r
            WHERE r.notificationId = n.id
              AND r.userId = :userId
          )
    """)
    List<Long> findUnreadBroadcastIdsForUser(Long userId);

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
