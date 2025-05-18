package com.example.betickettrain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.RouteStation}
 */
@Data
@NoArgsConstructor // ✅ BẮT BUỘC CHO JACKSON
@AllArgsConstructor
public class RouteStationDto implements Serializable {
    Integer routeStationId;
    Integer stopOrder;
    Integer arrivalOffset;
    Integer departureOffset;
    Float distanceFromOrigin;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}