package com.example.betickettrain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripSearchResult {
    private Integer tripId;
    private String tripCode;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String routeName;
    private String trainNumber;
    private String trainType;
    private String originStation;
    private String destinationStation;
    private String duration;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String amenities;
}
