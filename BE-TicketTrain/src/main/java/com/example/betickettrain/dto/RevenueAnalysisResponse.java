package com.example.betickettrain.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RevenueAnalysisResponse {
    private List<String> periods;
    private List<Double> revenue;
    private List<Double> growth;
    private List<Double> averageTicketPrice;
}
