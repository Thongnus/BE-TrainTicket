
package com.example.betickettrain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainDTO {
    private Long id;
    private String trainNumber;
    private String trainName;
    private String trainType;
    private Integer totalCarriages;
    private Boolean isActive;
    
    // Add any additional fields needed for the DTO
}