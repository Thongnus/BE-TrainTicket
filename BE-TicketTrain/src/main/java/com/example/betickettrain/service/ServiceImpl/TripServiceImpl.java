package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.anotation.LogAction;
import com.example.betickettrain.dto.TripDto;
import com.example.betickettrain.entity.Route;
import com.example.betickettrain.entity.Train;
import com.example.betickettrain.entity.Trip;
import com.example.betickettrain.exceptions.ErrorCode;
import com.example.betickettrain.mapper.TripMapper;
import com.example.betickettrain.repository.RouteRepository;
import com.example.betickettrain.repository.TrainRepository;
import com.example.betickettrain.repository.TripRepository;
import com.example.betickettrain.service.GenericCacheService;
import com.example.betickettrain.service.TripService;
import com.example.betickettrain.util.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TrainRepository trainRepository;
    private final RouteRepository routeRepository;

    private final GenericCacheService cacheService;
    private final TripMapper tripMapper; // ✅ NEW: Mapper

    private static final String ALL_TRIPS_KEY = "all";

    @Override
    @LogAction(action = Constants.Action.CREATE,entity = "Trip", description = " Create a trip")
    public TripDto createTrip(TripDto dto) {
        Train train = trainRepository.findById(dto.getTrain().getTrainId().longValue())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.TRAIN_NOT_FOUND.message));
        Route route = routeRepository.findById(dto.getRoute().getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND.message));

        if (tripRepository.existsByTripCode(dto.getTripCode())) {
            throw new RuntimeException("Mã chuyến đã tồn tại");
        }

        Trip trip = tripMapper.toEntity(dto);
        trip.setTrain(train);
        trip.setRoute(route);
        trip.setStatus(dto.getStatus() == null ? Trip.Status.scheduled : dto.getStatus());
        trip.setDelayMinutes(dto.getDelayMinutes() == null ? 0 : dto.getDelayMinutes());

        Trip saved = tripRepository.save(trip);
        cacheService.clearCache(Constants.Cache.CACHE_TRIP);
        return tripMapper.toDto(saved);
    }

    @Override
    @LogAction(action = Constants.Action.UPDATE,entity = "NewFeed", description = " Update a trip")
    public TripDto updateTrip(Integer id, TripDto dto) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến tàu"+id));

        if (!trip.getTripCode().equals(dto.getTripCode()) &&
                tripRepository.existsByTripCode(dto.getTripCode())) {
            throw new RuntimeException("Mã chuyến đã tồn tại");
        }

        Train train = trainRepository.findById(dto.getTrain().getTrainId().longValue())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tàu"+id));
        Route route = routeRepository.findById(dto.getRoute().getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tuyến đường"+id));

        tripMapper.partialUpdate(dto, trip);
        trip.setTrain(train);
        trip.setRoute(route);

        Trip updated = tripRepository.save(trip);
        cacheService.remove(Constants.Cache.CACHE_TRIP, id);
        cacheService.remove(Constants.Cache.CACHE_TRIP, ALL_TRIPS_KEY);
        return tripMapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public TripDto getTrip(Integer id) {
        TripDto cached = cacheService.get(Constants.Cache.CACHE_TRIP, id, TripDto.class);
        if (cached != null) return cached;

        TripDto dto = tripRepository.findById(id)
                .map(tripMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến tàu"+id));

        cacheService.put(Constants.Cache.CACHE_TRIP, id, dto);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripDto> getAllTrips() {
        List<TripDto> cached = cacheService.get(Constants.Cache.CACHE_TRIP, ALL_TRIPS_KEY);
        if (cached != null) return cached;

        List<TripDto> dtos = tripRepository.findAll().stream()
                .map(tripMapper::toDto)
                .toList();

        cacheService.put(Constants.Cache.CACHE_TRIP, ALL_TRIPS_KEY, dtos);
        return dtos;
    }

    @Override
    @LogAction(action = Constants.Action.UPDATE,entity = "Trip", description = " Update status of trip")
    public TripDto updateTripStatus(Integer id, Trip.Status status) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến tàu"+id));

        trip.setStatus(status);
        Trip updated = tripRepository.save(trip);
        cacheService.remove(Constants.Cache.CACHE_TRIP, id);
        cacheService.remove(Constants.Cache.CACHE_TRIP, ALL_TRIPS_KEY);
        return tripMapper.toDto(updated);
    }

    @Override
    @LogAction(action = Constants.Action.DELETE,entity = "Trip", description = " Delete a trip")
    public void deleteTrip(Integer id) {
        if (!tripRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy chuyến tàu"+id);
        }

        tripRepository.deleteById(id);
        cacheService.remove(Constants.Cache.CACHE_TRIP, id);
        cacheService.remove(Constants.Cache.CACHE_TRIP, ALL_TRIPS_KEY);
    }
}
