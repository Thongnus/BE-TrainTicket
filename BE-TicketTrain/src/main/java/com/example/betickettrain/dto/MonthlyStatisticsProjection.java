package com.example.betickettrain.dto;

public interface MonthlyStatisticsProjection {
    Double getTotalRevenueCurrentMonth();
    Double getRevenueGrowthPercentage();
    Integer getTicketsSoldCurrentMonth();
    Double getTicketGrowthPercentage();
    Integer getTotalUsers();
    Integer getActiveTrains();
    Integer getCompletedTrips();
    Integer getDelayedTrips();
    Integer getActiveRoutes();
}
