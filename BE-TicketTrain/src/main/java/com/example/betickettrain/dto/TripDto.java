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
}