package com.example.betickettrain.service;

import com.example.betickettrain.dto.PopularTripDto;
import com.example.betickettrain.dto.TrainRouteDto;
import com.example.betickettrain.dto.TripDto;
import com.example.betickettrain.dto.TripSearchResult;
import com.example.betickettrain.entity.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TripService {
    TripDto createTrip(TripDto dto);
    TripDto updateTrip(Integer id, TripDto dto);
    TripDto getTrip(Integer id);
    List<TripDto> getAllTrips();
    TripDto updateTripStatus(Integer id, Trip.Status status);
    void deleteTrip(Integer id);
    List<TripSearchResult> searchTrips(Integer originStationId, Integer destinationStationId, LocalDate departureDate, Integer passengers);

    Map<String, List<TripSearchResult>> searchRoundTrip(
            Integer departureId, Integer destinationId,
            LocalDate departureDate, LocalDate returnDate,
            Integer passengers);

    List<TrainRouteDto> findPopularRoutes(int limit);

    @Transactional
    void markTripDelayed(Integer tripId,Integer delayInMinutes,String delayReason);

    Page<TripDto> findTrips(String search, String status, Pageable pageable);
}
