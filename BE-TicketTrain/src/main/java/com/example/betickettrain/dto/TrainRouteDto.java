package com.example.betickettrain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TrainRouteDto {
    private Integer tripId;
    private String departure;
    private String arrival;
    private String duration;
    private String trainNumber;
    private Double averagePrice;

    public TrainRouteDto(Integer tripId, String departure, String arrival, LocalDateTime departureTime, LocalDateTime arrivalTime, String trainNumber, Double averagePrice) {
        this.tripId = tripId;
        this.departure = departure;
        this.arrival = arrival;
        this.trainNumber = trainNumber;
        this.averagePrice = averagePrice;
        this.duration = calculateDuration(departureTime, arrivalTime);
    }

    // Constructor để ánh xạ từ TrainRouteProjection
    public TrainRouteDto(TrainRouteProjection projection) {
        this.tripId = projection.getTripId();
        this.departure = projection.getDeparture();
        this.arrival = projection.getArrival();
        this.trainNumber = projection.getTrainNumber();
        this.averagePrice = projection.getAveragePrice();
        this.duration = calculateDuration(projection.getDepartureTime(), projection.getArrivalTime());
    }

    private String calculateDuration(LocalDateTime departureTime, LocalDateTime arrivalTime) {
        if (departureTime == null || arrivalTime == null) return "N/A";
        long diffMs = java.time.Duration.between(departureTime, arrivalTime).toMillis();
        long hours = diffMs / (1000 * 60 * 60);
        long minutes = (diffMs % (1000 * 60 * 60)) / (1000 * 60);
        return String.format("%dh%dp", hours, minutes);
    }
}