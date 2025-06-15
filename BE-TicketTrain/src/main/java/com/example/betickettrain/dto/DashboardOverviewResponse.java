package com.example.betickettrain.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardOverviewResponse {
    private double totalRevenue;
    private double revenueGrowth;

    private int totalTickets;
    private int ticketsLast24h;

    private double cancellationRate;
    private double cancellationRateChange;

    private int activeTrips;
    private int tripsChange;
}
