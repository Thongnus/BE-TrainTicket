package com.example.betickettrain.dto;

import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.SystemLog}
 */
@Value
public class SystemLogDto implements Serializable {
    Integer logId;
    String action;
    String entityType;
    Integer entityId;
    String description;
    String ipAddress;
    String userAgent;
    LocalDateTime logTime;
}