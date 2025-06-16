package com.example.betickettrain.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReportPayloadDTO {
    private int month;
    private int year;
    private DashboardOverviewResponse overview;
    private DailyRevenueResponse dailyRevenue;
    private List<PopularRouteDTO> popularRoutes;
    private List<TicketCarriageDistributionDTO> ticketDistribution;
    private RevenueAnalysisResponse revenueAnalysis;
}
