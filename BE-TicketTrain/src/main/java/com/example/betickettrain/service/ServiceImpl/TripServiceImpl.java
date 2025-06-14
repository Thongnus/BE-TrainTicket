package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.anotation.LogAction;
import com.example.betickettrain.dto.*;
import com.example.betickettrain.entity.*;
import com.example.betickettrain.exceptions.ErrorCode;
import com.example.betickettrain.mapper.TripMapper;
import com.example.betickettrain.repository.*;
import com.example.betickettrain.service.GenericCacheService;
import com.example.betickettrain.service.TripService;
import com.example.betickettrain.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TripServiceImpl implements TripService  {

    private final TripRepository tripRepository;
    private final TrainRepository trainRepository;
    private final RouteRepository routeRepository;
    private final CustomTripRepository customTripRepository;
    private final GenericCacheService cacheService;
    private final TripMapper tripMapper; // ✅ NEW: Mapper
    private final TripScheduleRepository tripScheduleRepository;
    private final RouteStationRepository routeStationRepository;
    private static final String ALL_TRIPS_KEY = "all";

    @Override
    @LogAction(action = Constants.Action.CREATE,entity = "Trip", description = " Create a trip")
    @Transactional
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
        generateTripSchedulesFromRoute(saved);
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
        TripDto cached = cacheService.get(Constants.Cache.CACHE_TRIP, id);
        if (cached != null) return cached;
        log.info(" ️️Lấy thông tin chuyến tàu từ DB");
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
        log.info(" ️️Lấy thông tin chuyến tàu từ DB");
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

    @Override
    public List<TripSearchResult> searchTrips(Integer originStationId, Integer destinationStationId, LocalDate departureDate, Integer passengers) {
        return customTripRepository.searchTrips(originStationId, destinationStationId, departureDate, passengers);
    }
    @Override
    public Map<String, List<TripSearchResult>> searchRoundTrip(
            Integer departureId, Integer destinationId,
            LocalDate departureDate, LocalDate returnDate,
            Integer passengers) {

        List<TripSearchResult> departureTrips = customTripRepository.searchTrips(
                departureId, destinationId, departureDate, passengers);

        List<TripSearchResult> returnTrips = customTripRepository.searchTrips(
                destinationId, departureId, returnDate, passengers);

        Map<String, List<TripSearchResult>> result = new HashMap<>();
        result.put("departureTrips", departureTrips);
        result.put("returnTrips", returnTrips);

        return result;
    }

    @Override
    public List<TrainRouteDto> findPopularRoutes(int limit) {

        List<TrainRouteProjection> projections = tripRepository.findPopularRoutes(limit);
        return projections.stream()
                .map(TrainRouteDto::new) // Ánh xạ từ TrainRouteProjection sang TrainRouteDto
                .collect(Collectors.toList());
    }



    private void generateTripSchedulesFromRoute(Trip trip) {
        List<RouteStation> stops = routeStationRepository.findByRouteRouteIdOrderByStopOrderAsc(
                trip.getRoute().getRouteId()
        );

        List<TripSchedule> schedules = stops.stream()
                .map(stop -> TripSchedule.builder()
                        .trip(trip)
                        .station(stop.getStation())
                        .scheduledArrival(trip.getDepartureTime().plusMinutes(stop.getArrivalOffset()))
                        .scheduledDeparture(trip.getDepartureTime().plusMinutes(stop.getDepartureOffset()))
                        .status(TripSchedule.Status.scheduled)
                        .build())
                .collect(Collectors.toList());


        tripScheduleRepository.saveAll(schedules);
    }

    @Transactional
    @Override
    public void markTripDelayed(Integer tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến tàu"));

        if (!trip.getStatus().equals(Trip.Status.scheduled)) {
            throw new IllegalStateException("Chỉ chuyến scheduled mới được đánh dấu trễ");
        }

        trip.setStatus(Trip.Status.delayed);
        tripRepository.save(trip);
    }
}
