package com.example.betickettrain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripTrackingDto {
    private String currentStation;
    private String status; // scheduled, delayed, arrived
    private String scheduledArrival;
    private String actualArrival;
    private String nextStation;
    private String estimatedNextArrival;
}
