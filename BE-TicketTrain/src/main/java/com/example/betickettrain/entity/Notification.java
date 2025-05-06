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
    private Integer notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('booking', 'payment', 'system', 'promotion', 'trip_update')")
    private NotificationType notificationType;

    private Integer relatedId;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isRead;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public enum NotificationType {
        booking, payment, system, promotion, trip_update
    }
}
