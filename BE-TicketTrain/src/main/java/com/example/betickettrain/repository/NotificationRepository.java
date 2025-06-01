package com.example.betickettrain.repository;

import com.example.betickettrain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserUserIdAndRelatedIdAndNotificationType(Long attr0, Integer relatedId, Notification.NotificationType notificationType);
}