package com.example.betickettrain.service;

import com.example.betickettrain.dto.RouteDto;
import java.util.List;
public interface RouteService {
    RouteDto createRoute(RouteDto dto);
    RouteDto updateRoute(Integer id, RouteDto dto);
    void deleteRoute(Integer id);
    RouteDto getRouteById(Integer id);
    List<RouteDto> getAllRoutes();
}
