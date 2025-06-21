package com.example.betickettrain.service;

import com.example.betickettrain.dto.RouteDto;
import com.example.betickettrain.dto.RouteStationDto;
import com.example.betickettrain.util.Constants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RouteStationService {
    @Transactional
    void saveRouteStations(List<RouteStationDto> dtos);

    @Transactional(readOnly = true)
    List<RouteStationDto> getStationsByRoute(Integer routeId);


    void deleteStationsByRoute(Integer routeId);


}
