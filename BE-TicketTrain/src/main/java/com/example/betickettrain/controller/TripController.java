package com.example.betickettrain.controller;

import com.example.betickettrain.dto.TripDto;
import com.example.betickettrain.entity.Trip;
import com.example.betickettrain.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
