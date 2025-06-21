package com.example.betickettrain.controller;

import com.example.betickettrain.dto.RouteDto;
import com.example.betickettrain.dto.RouteStationDto;
import com.example.betickettrain.dto.RouteWithStationsRequest;
import com.example.betickettrain.service.RouteService;
import com.example.betickettrain.service.ServiceImpl.RouteStationServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RouteController {
    private final RouteService routeService;
    private final RouteStationServiceImpl routeStationService;
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<RouteDto> createRoute(@RequestBody RouteDto routeDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(routeService.createRoute(routeDto));
    }

    @GetMapping
    public ResponseEntity<List<RouteDto>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteDto> getRouteById(@PathVariable Integer id) {
        return ResponseEntity.ok(routeService.getRouteById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RouteDto> updateRoute(@PathVariable Integer id, @RequestBody RouteDto routeDto) {
        return ResponseEntity.ok(routeService.updateRoute(id, routeDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Integer id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }

    // Route Station endpoints
    @PostMapping("/{routeId}/stations")
    public ResponseEntity<Void> addStationsToRoute(@PathVariable Integer routeId,
                                                   @RequestBody List<RouteStationDto> stationDtos) {
        // Ensure all stations are associated with the correct route ID
        stationDtos.forEach(station -> station.setRouteId(routeId));
        routeStationService.saveRouteStations(stationDtos);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{routeId}/stations")
    public ResponseEntity<List<RouteStationDto>> getStationsByRoute(@PathVariable Integer routeId) {
        return ResponseEntity.ok(routeStationService.getStationsByRoute(routeId));
    }

    @DeleteMapping("/{routeId}/stations")
    public ResponseEntity<Void> deleteStationsByRoute(@PathVariable Integer routeId) {
        routeStationService.deleteStationsByRoute(routeId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/with-stations")
    public ResponseEntity<?> createRouteWithStations(
            @Valid @RequestBody RouteWithStationsRequest request) {

        routeService.createRouteWithStations(
                request.getRoute(),
                request.getStations()
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PutMapping("/{id}/with-stations")
    public ResponseEntity<?> updateRouteWithStations(
            @PathVariable Integer id,
            @RequestBody RouteWithStationsRequest request) {
        routeService.updateRouteWithStations(id, request.getRoute(), request.getStations());
        return ResponseEntity.ok("Cập nhật thành công");
    }

}