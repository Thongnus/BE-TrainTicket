package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Route;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Route}
 */
@Value
public class RouteDto implements Serializable {
    Integer routeId;
    String routeName;
    Float distance;
    String description;
    Route.Status status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}