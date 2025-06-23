package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.dto.RouteDto;
import com.example.betickettrain.dto.RouteStationDto;
import com.example.betickettrain.entity.Route;
import com.example.betickettrain.entity.Station;
import com.example.betickettrain.exceptions.BusinessException;
import com.example.betickettrain.exceptions.ErrorCode;
import com.example.betickettrain.mapper.RouteMapper;
import com.example.betickettrain.repository.RouteRepository;
import com.example.betickettrain.repository.StationRepository;
import com.example.betickettrain.service.GenericCacheService;
import com.example.betickettrain.service.RouteService;
import com.example.betickettrain.service.RouteStationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

import static com.example.betickettrain.util.Constants.Cache.CACHE_ROUTE;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;
    private final GenericCacheService cacheService;
        private  final StationRepository stationRepository;
    private final RouteStationService routeStationService;
    private static final String ALL_KEY = "all";
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public RouteDto createRoute(RouteDto request) {
        Station origin = stationRepository.findById(request.getOriginStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Origin station not found"));

        Station dest = stationRepository.findById(request.getDestinationStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination station not found"));
        boolean exists = routeRepository.existsByOriginStation_StationIdAndDestinationStation_StationId(
                request.getOriginStationId(), request.getDestinationStationId());

        if (exists) {
            throw new BusinessException(ErrorCode.DUPLICATE_STATION.code,
                    "A route already exists between origin station " + origin.getStationName() +
                            " and destination station " + dest.getStationName());
        }
        Route entity = Route.builder()
                .routeName(request.getRouteName())
                .originStation(origin)
                .destinationStation(dest)
                .distance(request.getDistance())
                .description(request.getDescription())
                .status(request.getStatus())
                .build();

        Route saved = routeRepository.save(entity);
        cacheService.clearCache(CACHE_ROUTE);

        return routeMapper.toDto(saved);
    }

    @Override
    public RouteDto updateRoute(Integer id, RouteDto request) {
        Route existing = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found: " + id));

        Station origin = stationRepository.findById(request.getOriginStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Origin station not found"));
        Station dest = stationRepository.findById(request.getDestinationStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination station not found"));

        existing.setRouteName(request.getRouteName());
        existing.setOriginStation(origin);
        existing.setDestinationStation(dest);
        existing.setDistance(request.getDistance());
        existing.setDescription(request.getDescription());
        existing.setStatus(request.getStatus());

        Route saved = routeRepository.save(existing);
        cacheService.remove(CACHE_ROUTE, id);
        cacheService.remove(CACHE_ROUTE, ALL_KEY);

        return routeMapper.toDto(saved);
    }
    @Transactional
    @Override
    public void deleteRoute(Integer id) {
        if (!routeRepository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorCode.ROUTER_NOT_FOUND.message + id);
        }
        // Xoá các route_station trước
        routeStationService.deleteStationsByRoute(id);
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
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ROUTER_NOT_FOUND.message + id));

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
    @Transactional
    @Override
    public void createRouteWithStations(RouteDto routeDto, List<RouteStationDto> stationDtos) {
        try {
            // All operations in one transaction
            RouteDto createdRoute = createRoute(routeDto);

            stationDtos.forEach(station -> station.setRouteId(createdRoute.getRouteId()));
            routeStationService.saveRouteStations(stationDtos);
            cacheService.clearCache(CACHE_ROUTE);
            cacheService.remove(CACHE_ROUTE, ALL_KEY);

        } catch (Exception ex) {
            // Transaction will be rolled back automatically
            log.error("Failed to create route with stations", ex.getMessage() );
            throw new BusinessException("ROUTE_CREATION_FAILED", "Failed to create route with stations");
        }
    }

    @Transactional
    @Override
    public void updateRouteWithStations(Integer id, RouteDto routeDto, List<RouteStationDto> stationDtos) {
        try {
            // 1. Cập nhật thông tin tuyến đường (giống updateRoute)
            Route existing = routeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Route not found: " + id));

            Station origin = stationRepository.findById(routeDto.getOriginStationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Origin station not found"));
            Station dest = stationRepository.findById(routeDto.getDestinationStationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Destination station not found"));

            existing.setRouteName(routeDto.getRouteName());
            existing.setOriginStation(origin);
            existing.setDestinationStation(dest);
            existing.setDistance(routeDto.getDistance());
            existing.setDescription(routeDto.getDescription());
            existing.setStatus(routeDto.getStatus());

            Route saved = routeRepository.save(existing);

            // 2. Xoá danh sách RouteStation cũ
            routeStationService.deleteStationsByRoute(id);
            entityManager.flush();
            // 3. Gán lại routeId mới cho stationDtos rồi lưu
            stationDtos.forEach(dto -> dto.setRouteId(id));
            routeStationService.saveRouteStations(stationDtos);

            // 4. Xóa cache

            cacheService.remove(CACHE_ROUTE, id);
            cacheService.remove(CACHE_ROUTE, ALL_KEY);

        } catch (Exception ex) {
            log.error("Failed to update route with stations", ex);
            throw new BusinessException("ROUTE_UPDATE_FAILED", "Failed to update route with stations");
        }
    }
    public List<RouteDto> getRoutesByStatus(Route.Status status) {
        List<RouteDto> cached = cacheService.get(CACHE_ROUTE, status.name());
        if (cached != null) return cached;

        List<RouteDto> dtos = routeRepository.findAllByStatus(status).stream()
                .map(routeMapper::toDto)
                .toList();

        cacheService.put(CACHE_ROUTE, status.name(), dtos);
        return dtos;
    }
}
