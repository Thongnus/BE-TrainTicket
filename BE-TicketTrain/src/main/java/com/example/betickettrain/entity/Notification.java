package com.example.betickettrain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, columnDefinition = "ENUM('booking', 'payment', 'system', 'promotion', 'trip_update')")
    private NotificationType notificationType;

    @Column(name = "related_id")
    private Integer relatedId;

    @Column(name = "is_read", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isRead;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum NotificationType {
        booking, payment, system, promotion, trip_update
    }
}
