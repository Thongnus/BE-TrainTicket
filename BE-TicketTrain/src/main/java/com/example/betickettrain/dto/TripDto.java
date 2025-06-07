package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Trip;
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
    LocalDateTime departureTime;
    LocalDateTime arrivalTime;
    Trip.Status status;
    Integer delayMinutes;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
     String origin;
     String destination;
    public TripDto(String trainNumber, LocalDateTime departureTime, LocalDateTime arrivalTime, String origin, String destination) {
        this.train.trainNumber = trainNumber;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.origin = origin;
        this.destination = destination;
    }
}