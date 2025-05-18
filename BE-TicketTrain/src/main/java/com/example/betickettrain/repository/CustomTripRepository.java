package com.example.betickettrain.repository;

import com.example.betickettrain.dto.TripSearchResult;
import java.time.LocalDate;
import java.util.List;
public interface CustomTripRepository {
    List<TripSearchResult> searchTrips(Integer originStationId, Integer destinationStationId, LocalDate departureDate, Integer passengers);
}
