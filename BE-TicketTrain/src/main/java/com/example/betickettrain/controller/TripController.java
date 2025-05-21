package com.example.betickettrain.controller;

import com.example.betickettrain.dto.*;
import com.example.betickettrain.entity.Trip;
import com.example.betickettrain.service.SeatService;
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
    private final SeatService seatService;
    @PostMapping
    public Response<?> createTrip(@RequestBody TripDto dto) {
        return  new Response<>(tripService.createTrip(dto));
    }

    @PutMapping("/{id}")
    public Response<?> updateTrip(@PathVariable Integer id, @RequestBody TripDto dto) {
        return new Response<>(tripService.updateTrip(id, dto));
    }

    @GetMapping
    public  Response<?> getAllTrips() {
        return new Response<>(tripService.getAllTrips());
    }

    @GetMapping("/{id}")
    public  Response<?> getTrip(@PathVariable Integer id) {
        return new Response<>(tripService.getTrip(id));
    }

    @PatchMapping("/{id}/status")
    public  Response<?> updateTripStatus(@PathVariable Integer id, @RequestParam Trip.Status status) {
        return new Response<>(tripService.updateTripStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrip(@PathVariable Integer id) {
        tripService.deleteTrip(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/search")
    public  Response<?> searchTrips(
            @RequestParam("origin") Integer originStationId,
            @RequestParam("destination") Integer destinationStationId,
            @RequestParam("date") LocalDate departureDate,
            @RequestParam("passengers") Integer passengers
    ) {
        return new Response<>(tripService.searchTrips(originStationId, destinationStationId, departureDate, passengers));
    }



    @GetMapping("/{tripId}/carriages-with-seats")
    public Response<List<CarriageSeatDto>> getCarriagesWithSeats(@PathVariable Integer tripId) {
        return new Response<>(seatService.getCarriagesWithSeats(tripId));
    }
}
