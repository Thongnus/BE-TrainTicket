package com.example.betickettrain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PopularTripDto {
    private Integer tripId;
    private String tripCode;
    private String routeName;
    private String originStation;
    private String destinationStation;
    private Long totalTickets;
    private Double averageRating;
}