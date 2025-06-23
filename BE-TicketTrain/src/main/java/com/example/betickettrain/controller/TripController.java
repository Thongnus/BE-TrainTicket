package com.example.betickettrain.controller;

import com.example.betickettrain.dto.*;
import com.example.betickettrain.entity.Trip;
import com.example.betickettrain.service.SeatService;
import com.example.betickettrain.service.TripService;
import com.example.betickettrain.service.TripTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TripController {
    private final TripService tripService;
    private final SeatService seatService;
    private final TripTrackingService tripTrackingService;

    @PostMapping
    public Response<?> createTrip(@RequestBody TripDto dto) {
        return new Response<>(tripService.createTrip(dto));
    }

    @PutMapping("/{id}")
    public Response<?> updateTrip(@PathVariable Integer id, @RequestBody TripDto dto) {
        return new Response<>(tripService.updateTrip(id, dto));
    }

    @GetMapping
    public Response<?> getAllTrips() {
        return new Response<>(tripService.getAllTrips());
    }

    @GetMapping("/{id}")
    public Response<?> getTrip(@PathVariable Integer id) {
        return new Response<>(tripService.getTrip(id));
    }

    @PutMapping("/{id}/status")
    public Response<?> updateTripStatus(@PathVariable Integer id, @RequestParam Trip.Status status) {
        return new Response<>(tripService.updateTripStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrip(@PathVariable Integer id) {
        tripService.deleteTrip(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public Response<?> searchTrips(
            @RequestParam("departure") Integer originStationId,
            @RequestParam("destination") Integer destinationStationId,
            @RequestParam("departureDate") LocalDate departureDate,
            @RequestParam("passengers") Integer passengers,
            @RequestParam(value = "returnDate", required = false) LocalDate returnDate
    ) { // sau nay bo sung validate ở Be
        if (returnDate != null) {
            return new Response<>(tripService.searchRoundTrip(
                    originStationId, destinationStationId,
                    departureDate, returnDate, passengers
            ));
        } else {
            return new Response<>(tripService.searchTrips(
                    originStationId, destinationStationId,
                    departureDate, passengers
            ));
        }
    }


    @GetMapping("/{tripId}/carriages-with-seats")
    public Response<List<TripWithSeatsDto>> getCarriagesWithSeats(@PathVariable Integer tripId ) {
        return new Response<>(seatService.getCarriagesWithSeats(tripId));
    }


    @GetMapping("/{tripId}/tracking")
    public TripTrackingDto getTracking(@PathVariable Integer tripId) {
        return tripTrackingService.getTripTracking(tripId);
    }
    @GetMapping("/popular")
    public ResponseEntity<List<TrainRouteDto>> getPopularRoutes(@RequestParam(defaultValue = "12") int limit ) {
        List<TrainRouteDto> popularRoutes = tripService.findPopularRoutes(limit);
        return ResponseEntity.ok(popularRoutes);
    }
    @PostMapping("/{tripId}/stations/{stationId}/arrived")
    public void markStationArrived(
            @PathVariable Integer tripId,
            @PathVariable Integer stationId,
            @RequestParam("actualArrival") LocalDateTime actualArrival
    ) {
        tripTrackingService.markStationArrived(tripId, stationId, actualArrival);
    }

    @PostMapping("/{tripId}/stations/{stationId}/departed")
    public void markStationDeparted(
            @PathVariable Integer tripId,
            @PathVariable Integer stationId,
            @RequestParam("actualDeparture") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime actualDeparture
    ) {
        tripTrackingService.markStationDeparted(tripId, stationId, actualDeparture);
    }

    @PostMapping("/{tripId}/delay")
    public ResponseEntity<String> markDelayed(@PathVariable Integer tripId) {
        tripService.markTripDelayed(tripId);
        return ResponseEntity.ok("Chuyến đã được đánh dấu là trễ.");
    }
    @GetMapping("/paged/search")
    public ResponseEntity<Page<TripDto>> searchTrips(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "departureTime,desc") String[] sort
    ) {

        // Tạo đối tượng Pageable
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<TripDto> result = tripService.findTrips(search, status, pageable);
        return ResponseEntity.ok(result);
    }

    // Helper method để xử lý sort param
    private Sort parseSort(String[] sort) {
        if (sort.length == 2) {
            return Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        } else {
            return Sort.by("departureTime").descending();
        }
    }
}
