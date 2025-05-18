package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Route;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Route}
 */
@Data
@NoArgsConstructor // ✅ BẮT BUỘC CHO JACKSON
@AllArgsConstructor
public class RouteDto implements Serializable {
    Integer routeId;
    String routeName;
    Float distance;
    String description;
    Route.Status status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}