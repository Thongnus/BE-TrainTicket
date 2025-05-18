package com.example.betickettrain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Setting}
 */
@Data
@NoArgsConstructor // ✅ BẮT BUỘC CHO JACKSON
@AllArgsConstructor
public class SettingDto implements Serializable {
    Integer settingId;
    String settingKey;
    String settingValue;
    String settingGroup;
    String description;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}