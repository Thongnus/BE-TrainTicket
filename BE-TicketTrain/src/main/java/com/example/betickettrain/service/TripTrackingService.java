package com.example.betickettrain.service;

import com.example.betickettrain.dto.TripTrackingDto;

import java.time.LocalDateTime;

public interface TripTrackingService {
    TripTrackingDto getTripTracking(Integer tripId);
    void markStationArrived(Integer tripId, Integer stationId, LocalDateTime actualArrival);
    void markStationDeparted(Integer tripId, Integer stationId, LocalDateTime actualDeparture);
}
