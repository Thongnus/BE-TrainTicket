package com.example.betickettrain.service;

import com.example.betickettrain.dto.RouteStationDto;
import com.example.betickettrain.util.Constants;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RouteStationService {
    @Transactional
    void saveRouteStations(List<RouteStationDto> dtos);

    @Transactional(readOnly = true)
    List<RouteStationDto> getStationsByRoute(Integer routeId);

    @Transactional
    void deleteStationsByRoute(Integer routeId);
}
