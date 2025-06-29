package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Carriage;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link com.example.betickettrain.entity.Carriage}
 */
@Data
@NoArgsConstructor // ✅ BẮT BUỘC CHO JACKSON
@AllArgsConstructor
public class CarriageDto implements Serializable {
    Integer carriageId;
    String carriageNumber;
    private Integer trainId;
    private String trainName;
    Carriage.CarriageType carriageType;
    Integer capacity;
    Carriage.Status status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
}