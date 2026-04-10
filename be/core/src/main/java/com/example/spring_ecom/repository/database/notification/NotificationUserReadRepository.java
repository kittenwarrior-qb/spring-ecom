package com.example.spring_ecom.repository.database.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationUserReadRepository extends JpaRepository<NotificationUserReadEntity, Long> {

    @Query("""
        SELECT r.notificationId
        FROM NotificationUserReadEntity r
        WHERE r.userId = :userId
          AND r.notificationId IN :notificationIds
    """)
    List<Long> findReadNotificationIds(Long userId, List<Long> notificationIds);
}

