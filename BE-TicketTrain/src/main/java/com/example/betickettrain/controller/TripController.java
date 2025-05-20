package com.example.betickettrain.controller;

import com.example.betickettrain.dto.TripDto;
import com.example.betickettrain.dto.TripSearchResult;
import com.example.betickettrain.entity.Trip;
import com.example.betickettrain.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;

    @PostMapping
    public ResponseEntity<?> createTrip(@RequestBody TripDto dto) {
        return ResponseEntity.ok(tripService.createTrip(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTrip(@PathVariable Integer id, @RequestBody TripDto dto) {
        return ResponseEntity.ok(tripService.updateTrip(id, dto));
    }

    @GetMapping
    public ResponseEntity<?> getAllTrips() {
        return ResponseEntity.ok(tripService.getAllTrips());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTrip(@PathVariable Integer id) {
        return ResponseEntity.ok(tripService.getTrip(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateTripStatus(@PathVariable Integer id, @RequestParam Trip.Status status) {
        return ResponseEntity.ok(tripService.updateTripStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrip(@PathVariable Integer id) {
        tripService.deleteTrip(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/search")
    public List<TripSearchResult> searchTrips(
            @RequestParam("origin") Integer originStationId,
            @RequestParam("destination") Integer destinationStationId,
            @RequestParam("date") LocalDate departureDate,
            @RequestParam("passengers") Integer passengers
    ) {
        return tripService.searchTrips(originStationId, destinationStationId, departureDate, passengers);
    }
}
