package com.example.betickettrain.dto;

import lombok.Data;

import java.util.List;

@Data
public class RouteWithStationsRequest {
    private RouteDto route;
    private List<RouteStationDto> stations;
}
