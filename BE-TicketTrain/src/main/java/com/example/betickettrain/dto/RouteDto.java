package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Route;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    private Integer originStationId;       // Gửi lên khi create
    private Integer destinationStationId;
    private String originStationName;       // Gửi lên khi create
    private String destinationStationName; // Gửi lên khi create
    Float distance;
    String description;
    Route.Status status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
}