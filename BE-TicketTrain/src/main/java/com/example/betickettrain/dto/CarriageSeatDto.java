package com.example.betickettrain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarriageSeatDto {
    private Integer carriageId;
    private String carriageNumber;
    private String carriageType;
    private Integer capacity;
    private List<SeatDto> seats;
}
