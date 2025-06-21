package com.example.betickettrain.service;

import com.example.betickettrain.dto.RouteDto;
import com.example.betickettrain.dto.RouteStationDto;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
public interface RouteService {
    RouteDto createRoute(RouteDto dto);
    RouteDto updateRoute(Integer id, RouteDto dto);
    void deleteRoute(Integer id);
    RouteDto getRouteById(Integer id);
    List<RouteDto> getAllRoutes();

    @Transactional
    void createRouteWithStations(RouteDto routeDto, List<RouteStationDto> stationDtos);

    @Transactional
    void updateRouteWithStations(Integer id, RouteDto routeDto, List<RouteStationDto> stationDtos);
}
