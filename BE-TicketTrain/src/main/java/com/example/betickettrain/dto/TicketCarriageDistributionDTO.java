package com.example.betickettrain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketCarriageDistributionDTO {
    private String name;
    private Long count;
    private Double percentage;
}
