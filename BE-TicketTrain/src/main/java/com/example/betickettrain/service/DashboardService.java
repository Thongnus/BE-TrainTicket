package com.example.betickettrain.service;

import com.example.betickettrain.dto.*;

import java.util.List;

public interface DashboardService {
    DashboardOverviewResponse getOverview();

    DailyRevenueResponse getDailyRevenue();

    List<PopularRouteDTO> getPopularRoutes();

    List<TicketCarriageDistributionDTO> getTicketDistributionByCarriage();

    RevenueAnalysisResponse getRevenueAnalysis();
}
