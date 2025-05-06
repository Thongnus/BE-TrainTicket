package com.example.betickettrain.dto;

import com.example.betickettrain.entity.TripSchedule;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.TripSchedule}
 */
@Value
public class TripScheduleDto implements Serializable {
    Integer scheduleId;
    TripDto trip;
    StationDto station;
    LocalDateTime scheduledArrival;
    LocalDateTime scheduledDeparture;
    LocalDateTime actualArrival;
    LocalDateTime actualDeparture;
    TripSchedule.Status status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}