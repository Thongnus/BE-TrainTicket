package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.anotation.LogAction;
import com.example.betickettrain.dto.StationDto;
import com.example.betickettrain.entity.Station;

import com.example.betickettrain.exceptions.ErrorCode;
import com.example.betickettrain.mapper.StationMapper;
import com.example.betickettrain.repository.StationRepository;
import com.example.betickettrain.service.GenericCacheService;
import com.example.betickettrain.service.StationService;
import com.example.betickettrain.util.Constants;
import io.netty.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.betickettrain.util.Constants.Cache.CACHE_STATION;

@Service
@RequiredArgsConstructor
@Slf4j
public class StationServiceImpl implements StationService {

    private final StationRepository stationRepository;
    private final StationMapper stationMapper;
    private final GenericCacheService cacheService;

    private static final String ALL_STATIONS_KEY = "all";
    private static final String ALL_PAGE_STATIONS_KEY = "page_all";
    @Override
    public List<StationDto> getAllStations() {
        // Check cache first
        List<StationDto> cachedStations = cacheService.get(CACHE_STATION, ALL_STATIONS_KEY);
        
        if (cachedStations != null) {
            return cachedStations;
        }
        // Cache miss - fetch from database
        List<StationDto> stations = stationRepository.findAll().stream()
                .map(stationMapper::toDto)
                .collect(Collectors.toList());
        
        // Save to cache
        cacheService.put(CACHE_STATION, ALL_STATIONS_KEY, stations);
        
        return stations;
    }

    @Override
    public StationDto getStationById(Integer id) {
        // Check cache first
        StationDto cachedStation = cacheService.get(CACHE_STATION, id);
        
        if (cachedStation != null) {
            return cachedStation;
        }
        
        // Cache miss - fetch from database
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Station not found with id: " + id));
        
        StationDto stationDto = stationMapper.toDto(station);
        
        // Save to cache
        cacheService.put(CACHE_STATION, id, stationDto);
        
        return stationDto;
    }

    @Override
    public List<StationDto> getStationsByStatus(Station.Status status) {
        String cacheKey = "status_" + status;
        
        // Check cache first
        List<StationDto> cachedStations = cacheService.get(CACHE_STATION, cacheKey);
        
        if (cachedStations != null) {
            return cachedStations;
        }
        
        // Cache miss - fetch from database
        List<StationDto> stations = stationRepository.findByStatus(status).stream()
                .map(stationMapper::toDto)
                .collect(Collectors.toList());
        
        // Save to cache
        cacheService.put(CACHE_STATION, cacheKey, stations);
        
        return stations;
    }

    @Override
    public List<StationDto> searchStations(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllStations();
        }
        
        String cacheKey = "search_" + keyword.toLowerCase();
        
        // Check cache first
        List<StationDto> cachedResults = cacheService.get(CACHE_STATION, cacheKey);
        
        if (cachedResults != null) {
            return cachedResults;
        }
        
        // Cache miss - search in database
        List<Station> nameResults = stationRepository.findByStationNameContainingIgnoreCase(keyword);
        List<Station> cityResults = stationRepository.findByCityContainingIgnoreCase(keyword);
        List<Station> provinceResults = stationRepository.findByProvinceContainingIgnoreCase(keyword);
        
        // Combine all results and remove duplicates
        List<StationDto> results = Stream.of(nameResults, cityResults, provinceResults)
                .flatMap(List::stream)
                .distinct()
                .map(stationMapper::toDto)
                .collect(Collectors.toList());
        
        // Save to cache
        cacheService.put(CACHE_STATION, cacheKey, results);
        
        return results;
    }

    @Override
    @Transactional
    @LogAction(action = Constants.Action.CREATE,entity = "Station", description = "Create a new station")
    public StationDto createStation(StationDto stationDto) {
        // Check if station with same name already exists
        if (stationRepository.existsByStationNameIgnoreCase(stationDto.getStationName())) {
            throw new IllegalArgumentException("Station with name '" + stationDto.getStationName() + "' already exists");
        }
        
        Station station = stationMapper.toEntity(stationDto);
        
        // Set default status if not provided
        if (station.getStatus() == null) {
            station.setStatus(Station.Status.active);
        }
        
        Station savedStation = stationRepository.save(station);
        StationDto result = stationMapper.toDto(savedStation);
        
        // Invalidate caches
        cacheService.remove(CACHE_STATION, ALL_STATIONS_KEY);
        if (savedStation.getStatus() != null) {
            cacheService.remove(CACHE_STATION, "status_" + savedStation.getStatus());
        }
        
        return result;
    }

    @Override
    @Transactional
    @LogAction(action = Constants.Action.UPDATE,entity = "Station", description = " Update a station")
    public StationDto updateStation(Integer id, StationDto stationDto) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.TRAIN_NOT_FOUND.message + id));
        
        // Check if trying to update to an existing station name
        if (!station.getStationName().equalsIgnoreCase(stationDto.getStationName()) && 
                stationRepository.existsByStationNameIgnoreCase(stationDto.getStationName())) {
            throw new IllegalArgumentException("Station with name '" + stationDto.getStationName() + "' already exists");
        }
        
        // Store the old status for cache invalidation
        Station.Status oldStatus = station.getStatus();
        
        // Update the station from the DTO
        stationMapper.partialUpdate(stationDto, station);
        
        Station updatedStation = stationRepository.save(station);
        StationDto result = stationMapper.toDto(updatedStation);
        
        // Invalidate caches
        cacheService.remove(CACHE_STATION, id);
        cacheService.remove(CACHE_STATION, ALL_STATIONS_KEY);
    //    cacheService.remove(CACHE_STATION,ALL_PAGE_STATIONS_KEY);
        // Invalidate status caches if status changed
        if (oldStatus != null) {
            cacheService.remove(CACHE_STATION, "status_" + oldStatus);
        }
        if (updatedStation.getStatus() != null) {
            cacheService.remove(CACHE_STATION, "status_" + updatedStation.getStatus());
        }
        
        return result;
    }

    @Override
    @Transactional
    @LogAction(action = Constants.Action.DELETE,entity = "Station", description = "Delete a station")
    public void deleteStation(Integer id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.TRAIN_NOT_FOUND.message + id));
        
        // Store the status for cache invalidation
        Station.Status status = station.getStatus();
        
        stationRepository.deleteById(id);
        
        // Invalidate caches
        cacheService.remove(CACHE_STATION, id);
        cacheService.remove(CACHE_STATION, ALL_STATIONS_KEY);
       // cacheService.remove(CACHE_STATION,ALL_PAGE_STATIONS_KEY);
        if (status != null) {
            cacheService.remove(CACHE_STATION, "status_" + status);
        }
    }

    @Override
    public Page<StationDto> getStationsPaged(int page, int size) {
//        Page<StationDto> cachedStations = cacheService.get(CACHE_STATION, ALL_PAGE_STATIONS_KEY, Page.class);
//
//        if (cachedStations != null) {
//            return cachedStations;
//        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("stationName").ascending());
        Page<Station> stationPage = stationRepository.findAll(pageable);
        log.info(" ️️Lấy thông tin train từ DB");
        // Save to cache
     //   cacheService.put(CACHE_STATION, ALL_PAGE_STATIONS_KEY, stationPage);

        return stationPage.map(stationMapper::toDto);
    }
}