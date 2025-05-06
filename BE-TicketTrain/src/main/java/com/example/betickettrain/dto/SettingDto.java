package com.example.betickettrain.dto;

import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Setting}
 */
@Value
public class SettingDto implements Serializable {
    Integer settingId;
    String settingKey;
    String settingValue;
    String settingGroup;
    String description;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}