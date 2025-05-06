package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Station;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Station}
 */
@Value
public class StationDto implements Serializable {
    Integer stationId;
    String stationName;
    String location;
    String address;
    String city;
    String province;
    String phone;
    Station.Status status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}