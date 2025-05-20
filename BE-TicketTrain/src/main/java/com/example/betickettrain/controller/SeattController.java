package com.example.betickettrain.controller;

import com.example.betickettrain.dto.CarriageSeatDto;
import com.example.betickettrain.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class SeattController {

    private final SeatService seatService;

    @GetMapping("/{tripId}/carriages-with-seats")
    public List<CarriageSeatDto> getCarriagesWithSeats(@PathVariable Integer tripId) {
        return seatService.getCarriagesWithSeats(tripId);
    }

}
