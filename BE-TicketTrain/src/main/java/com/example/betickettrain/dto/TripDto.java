package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Trip;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Trip}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripDto implements Serializable {
    Integer tripId;
    RouteDto route;
    TrainDto train;
    String tripCode;
    @JsonFormat(pattern = "HH:mm:ss dd/MM/yyyy ")
    LocalDateTime departureTime;
    @JsonFormat(pattern = "HH:mm:ss dd/MM/yyyy")
    LocalDateTime arrivalTime;
    Trip.Status status;
    Integer delayMinutes;
    @JsonFormat(pattern = "HH:mm:ss dd/MM/yyyy")
    LocalDateTime createdAt;
    @JsonFormat(pattern = "HH:mm:ss dd/MM/yyyy")
    LocalDateTime updatedAt;

    public TripDto(String trainNumber, LocalDateTime departureTime, LocalDateTime arrivalTime) {
        this.train.trainNumber = trainNumber;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;

    }
}