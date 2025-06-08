package com.example.betickettrain.controller;

import com.example.betickettrain.dto.MonthlyStatisticsProjection;
import com.example.betickettrain.dto.Response;
import com.example.betickettrain.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/stats")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class StatisticsController {
    private final StatisticsService service;

    @GetMapping("/monthly")
    public Response<MonthlyStatisticsProjection> getStats() {
        return new Response<>(service.getStats());
    }
}