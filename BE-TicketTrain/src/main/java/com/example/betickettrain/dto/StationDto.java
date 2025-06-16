package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Station;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Station}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationDto implements Serializable {
    Integer stationId;
    String stationName;
    String location;
    String address;
    String city;
    String province;
    String phone;
    Station.Status status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
}