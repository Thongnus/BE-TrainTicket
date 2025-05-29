package com.example.betickettrain.dto;

import java.time.LocalDateTime;

public interface TrainRouteProjection {
    Integer getTripId();
    String getDeparture();
    String getArrival();
    LocalDateTime getDepartureTime();
    LocalDateTime getArrivalTime();
    String getTrainNumber();
    Double getAveragePrice();
}