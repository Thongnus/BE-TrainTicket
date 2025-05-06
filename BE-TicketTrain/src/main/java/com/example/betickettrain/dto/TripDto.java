package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Trip;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Trip}
 */
@Value
public class TripDto implements Serializable {
    Integer tripId;
    RouteDto route;
    TrainDTO train;
    String tripCode;
    LocalDateTime departureTime;
    LocalDateTime arrivalTime;
    Trip.Status status;
    Integer delayMinutes;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}