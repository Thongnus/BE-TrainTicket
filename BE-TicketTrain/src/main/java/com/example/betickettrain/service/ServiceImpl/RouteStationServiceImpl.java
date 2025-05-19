package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.anotation.LogAction;
import com.example.betickettrain.dto.RouteStationDto;
import com.example.betickettrain.entity.RouteStation;
import com.example.betickettrain.mapper.RouteStationMapper;
import com.example.betickettrain.repository.RouteStationRepository;
import com.example.betickettrain.service.GenericCacheService;
import com.example.betickettrain.service.RouteStationService;
import com.example.betickettrain.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteStationServiceImpl implements RouteStationService {

    private final RouteStationRepository routeStationRepository;
    private final RouteStationMapper routeStationMapper;
  //  private final GenericCacheService cacheService;



    @Transactional
    @Override
    public void saveRouteStations(List<RouteStationDto> dtos) {
        List<RouteStation> entities = dtos.stream()
                .map(routeStationMapper::toEntity)
                .collect(Collectors.toList());
        routeStationRepository.saveAll(entities);
    }

    @Transactional(readOnly = true)
    @Override
    public List<RouteStationDto> getStationsByRoute(Integer routeId) {

        return routeStationRepository.findByRouteRouteIdOrderByStopOrderAsc(routeId).stream()
                .map(routeStationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @LogAction(action = Constants.Action.DELETE, entity = "RouteStation", description = "Delete all stations in route")
    @Override
    public void deleteStationsByRoute(Integer routeId) {
        routeStationRepository.deleteByRoute_RouteId(routeId);

    }
}
