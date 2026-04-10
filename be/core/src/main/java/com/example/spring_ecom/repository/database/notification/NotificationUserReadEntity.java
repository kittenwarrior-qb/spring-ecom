package com.example.spring_ecom.repository.database.notification;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "notification_user_reads",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_notification_user_reads", columnNames = {"notification_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_notification_user_reads_user", columnList = "user_id"),
                @Index(name = "idx_notification_user_reads_notification", columnList = "notification_id")
        }
)
public class NotificationUserReadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_id", nullable = false)
    private Long notificationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @CreationTimestamp
    @Column(name = "read_at", nullable = false, updatable = false)
    private LocalDateTime readAt;
}

