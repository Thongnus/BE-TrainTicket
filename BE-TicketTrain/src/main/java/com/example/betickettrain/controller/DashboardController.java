package com.example.betickettrain.controller;

import com.example.betickettrain.dto.*;
import com.example.betickettrain.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    public ResponseEntity<DashboardOverviewResponse> getOverview() {
        return ResponseEntity.ok(dashboardService.getOverview());
    }
    @GetMapping("/daily-revenue")
    public ResponseEntity<DailyRevenueResponse> getDailyRevenue() {
        return ResponseEntity.ok(dashboardService.getDailyRevenue());
    }
    @GetMapping("/popular-routes")
    public ResponseEntity<List<PopularRouteDTO>> getPopularRoutes() {
        return ResponseEntity.ok(dashboardService.getPopularRoutes());
    }
    @GetMapping("/ticket-distribution")
    public ResponseEntity<List<TicketCarriageDistributionDTO>> getTicketDistribution() {
        return ResponseEntity.ok(dashboardService.getTicketDistributionByCarriage());
    }
    @GetMapping("/revenue-analysis")
    public ResponseEntity<RevenueAnalysisResponse> getRevenueAnalysis() {
        return ResponseEntity.ok(dashboardService.getRevenueAnalysis());
    }


}
