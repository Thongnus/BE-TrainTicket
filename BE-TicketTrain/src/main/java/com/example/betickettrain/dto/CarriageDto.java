package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Carriage;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Carriage}
 */
@Value
public class CarriageDto implements Serializable {
    Integer carriageId;
    String carriageNumber;
    Carriage.CarriageType carriageType;
    Integer capacity;
    Carriage.Status status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}