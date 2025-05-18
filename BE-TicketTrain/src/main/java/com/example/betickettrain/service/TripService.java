package com.example.betickettrain.service;

import com.example.betickettrain.dto.TripDto;
import com.example.betickettrain.entity.Trip;
import java.util.List;
public interface TripService {
    TripDto createTrip(TripDto dto);
    TripDto updateTrip(Integer id, TripDto dto);
    TripDto getTrip(Integer id);
    List<TripDto> getAllTrips();
    TripDto updateTripStatus(Integer id, Trip.Status status);
    void deleteTrip(Integer id);

}
