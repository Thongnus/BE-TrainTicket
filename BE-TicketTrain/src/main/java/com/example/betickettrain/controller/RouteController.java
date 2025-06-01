package com.example.betickettrain.controller;

import com.example.betickettrain.dto.RouteDto;
import com.example.betickettrain.dto.RouteStationDto;
import com.example.betickettrain.service.RouteService;
import com.example.betickettrain.service.ServiceImpl.RouteStationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RouteController {

    private final RouteService routeService;
    private final RouteStationServiceImpl routeStationService;

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
    public ResponseEntity<Map<String, Object>> createRouteWithStations(
            @RequestBody Map<String, Object> requestBody) {
        
        RouteDto routeDto = routeService.createRoute((RouteDto) requestBody.get("route"));
        
        @SuppressWarnings("unchecked")
        List<RouteStationDto> stationDtos = (List<RouteStationDto>) requestBody.get("stations");
        
        // Set the route ID for all stations
        stationDtos.forEach(station -> station.setRouteId(routeDto.getRouteId()));
        
        // Save the stations
        routeStationService.saveRouteStations(stationDtos);
        
        // Return both the route and its stations
        Map<String, Object> response = Map.of(
            "route", routeDto,
            "stations", routeStationService.getStationsByRoute(routeDto.getRouteId())
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}