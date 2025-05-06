package com.example.betickettrain.dto;

import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.RouteStation}
 */
@Value
public class RouteStationDto implements Serializable {
    Integer routeStationId;
    Integer stopOrder;
    Integer arrivalOffset;
    Integer departureOffset;
    Float distanceFromOrigin;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}