package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.dto.RouteDto;
import com.example.betickettrain.entity.Route;
import com.example.betickettrain.exceptions.ErrorCode;
import com.example.betickettrain.mapper.RouteMapper;
import com.example.betickettrain.repository.RouteRepository;
import com.example.betickettrain.service.GenericCacheService;
import com.example.betickettrain.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

import static com.example.betickettrain.util.Constants.Cache.CACHE_ROUTE;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;
    private final GenericCacheService cacheService;


    private static final String ALL_KEY = "all";

    @Override
    public RouteDto createRoute(RouteDto dto) {
        Route entity = routeMapper.toEntity(dto);
        Route saved = routeRepository.save(entity);
        cacheService.clearCache(CACHE_ROUTE); // xoá cache cũ
        return routeMapper.toDto(saved);
    }

    @Override
    public RouteDto updateRoute(Integer id, RouteDto dto) {
        Route updated = routeRepository.findById(id)
                .map(existing -> {
                    routeMapper.partialUpdate(dto, existing);
                    return routeRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException(ErrorCode.ROUTER_NOT_FOUND.message + id));

        cacheService.remove(CACHE_ROUTE, id);
        cacheService.remove(CACHE_ROUTE, ALL_KEY);
        return routeMapper.toDto(updated);
    }

    @Override
    public void deleteRoute(Integer id) {
        if (!routeRepository.existsById(id)) {
            throw new RuntimeException(ErrorCode.ROUTER_NOT_FOUND.message + id);
        }
        routeRepository.deleteById(id);
        cacheService.remove(CACHE_ROUTE, id);
        cacheService.remove(CACHE_ROUTE, ALL_KEY);
    }

    @Override
    public RouteDto getRouteById(Integer id) {
        RouteDto cached = cacheService.get(CACHE_ROUTE, id, RouteDto.class);
        if (cached != null) return cached;

        RouteDto dto = routeRepository.findById(id)
                .map(routeMapper::toDto)
                .orElseThrow(() -> new RuntimeException(ErrorCode.ROUTER_NOT_FOUND.message + id));

        cacheService.put(CACHE_ROUTE, id, dto);
        return dto;
    }

    @Override
    public List<RouteDto> getAllRoutes() {
        List<RouteDto> cached = cacheService.get(CACHE_ROUTE, ALL_KEY);
        if (cached != null) return cached;

        List<RouteDto> dtos = routeRepository.findAll().stream()
                .map(routeMapper::toDto)
                .toList();

        cacheService.put(CACHE_ROUTE, ALL_KEY, dtos);
        return dtos;
    }
}
