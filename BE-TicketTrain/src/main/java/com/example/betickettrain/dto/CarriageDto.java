package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Carriage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Carriage}
 */
@Data
@NoArgsConstructor // ✅ BẮT BUỘC CHO JACKSON
@AllArgsConstructor
public class CarriageDto implements Serializable {
    Integer carriageId;
    String carriageNumber;
    Carriage.CarriageType carriageType;
    Integer capacity;
    Carriage.Status status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}