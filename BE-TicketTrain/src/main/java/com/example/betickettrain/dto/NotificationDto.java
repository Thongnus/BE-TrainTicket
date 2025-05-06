package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Notification;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Notification}
 */
@Value
public class NotificationDto implements Serializable {
    Integer notificationId;
    String title;
    String message;
    Notification.NotificationType notificationType;
    Integer relatedId;
    Boolean isRead;
    LocalDateTime createdAt;
}