package com.example.betickettrain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.SystemLog}
 */
@Data
@NoArgsConstructor // ✅ BẮT BUỘC CHO JACKSON
@AllArgsConstructor
public class SystemLogDto implements Serializable {
    Integer logId;
    String action;
    String entityType;
    Integer entityId;
    String description;
    String ipAddress;
    String userAgent;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime logTime;
}