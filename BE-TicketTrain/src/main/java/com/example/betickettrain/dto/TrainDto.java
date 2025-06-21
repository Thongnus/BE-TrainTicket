package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Train;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Train}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainDto implements Serializable {
    Integer trainId;
    String trainNumber;
    String trainName;
    Train.TrainType trainType;
    Integer capacity;
    Train.Status status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
}