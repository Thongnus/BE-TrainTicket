package com.example.betickettrain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departureTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalTime;
    private String routeName;
    private String trainNumber;
    private String trainName;
    private String trainType;
    private String originStation;
    private String destinationStation;
    private String duration;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String amenities;
    private Integer availableSeats;

}
