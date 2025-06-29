package com.example.betickettrain.controller;

import com.example.betickettrain.dto.CarriageSeatDto;
import com.example.betickettrain.dto.Response;
import com.example.betickettrain.dto.SeatDto;
import com.example.betickettrain.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;


    @PostMapping()
    public Response<?> createSeat(@RequestBody SeatDto seatDto) {
        return new Response<>(seatService.createSeat(seatDto));
    }

    @PutMapping("/{seatId}")
    public Response<?> updateSeat(@PathVariable Integer seatId, @RequestBody SeatDto seatDto) {
        return new Response<>(seatService.updateSeat(seatId, seatDto));
    }

    @GetMapping()
    public Response<?> getAllSeats() {
        return new Response<>(seatService.getAllSeats());
    }

    @GetMapping("/seats/{seatId}")
    public Response<?> getSeat(@PathVariable Integer seatId) {
        return new Response<>(seatService.getSeat(seatId));
    }

    @DeleteMapping("/{seatId}")
    public ResponseEntity<?> deleteSeat(@PathVariable Integer seatId) {
        seatService.deleteSeat(seatId);
        return ResponseEntity.ok().build();
    }
//    @GetMapping("/paged-seats")
//    public Response<?> getPagedSeats(
//            @RequestParam(defaultValue = "0") Integer page,
//            @RequestParam(defaultValue = "10") Integer size) {
//        return new Response<>(seatService.getPagedSeats(page, size));
//    }
}
