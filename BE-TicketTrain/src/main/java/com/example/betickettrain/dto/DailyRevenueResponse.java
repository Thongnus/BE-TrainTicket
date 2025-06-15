package com.example.betickettrain.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyRevenueResponse {
    private List<String> dates;
    private List<Double> revenue;
    private List<Integer> tickets;
}
