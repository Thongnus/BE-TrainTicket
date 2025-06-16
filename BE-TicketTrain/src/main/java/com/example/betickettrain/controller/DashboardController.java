package com.example.betickettrain.controller;

import com.example.betickettrain.dto.*;
import com.example.betickettrain.service.DashboardService;
import com.example.betickettrain.service.ExcelReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final ExcelReportService reportService;
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
    @PostMapping("/reports/generate")
    public ResponseEntity<Resource> generateExcelReport(@RequestBody ReportPayloadDTO dto) throws IOException {
        Path reportPath = reportService.generateReportFromClient(dto);
        Resource resource = new UrlResource(reportPath.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + reportPath.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
