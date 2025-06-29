package com.example.betickettrain.controller;

import com.example.betickettrain.dto.CarriageDto;
import com.example.betickettrain.dto.CarriageWithSeatsDto;
import com.example.betickettrain.dto.SeatDto;
import com.example.betickettrain.service.CarriageService;
import com.example.betickettrain.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carriages")
@RequiredArgsConstructor
public class CarriageController {

    private final CarriageService carriageService;
    private final SeatService seatService;
    @PostMapping
    public ResponseEntity<CarriageDto> createCarriage(@RequestBody CarriageDto dto) {
        return ResponseEntity.ok(carriageService.createCarriage(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarriageDto> updateCarriage(
            @PathVariable Integer id,
            @RequestBody CarriageDto dto) {
        return ResponseEntity.ok(carriageService.updateCarriage(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarriage(@PathVariable Integer id) {
        carriageService.deleteCarriage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarriageDto> getCarriageById(@PathVariable Integer id) {
        return ResponseEntity.ok(carriageService.getCarriageById(id));
    }

    @GetMapping
    public ResponseEntity<List<CarriageDto>> getAllCarriages() {
        return ResponseEntity.ok(carriageService.getAllCarriages());
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> countCarriageActive(@RequestParam String status) {
        return ResponseEntity.ok(carriageService.countCarriageActive(status));
    }

    @GetMapping("/paged/search")
    public ResponseEntity<Page<CarriageDto>> getPagedCarriages(@RequestParam(required = false) String search,
                                                               @RequestParam(required = false, defaultValue = "all") String status,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size,
                                                               @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<CarriageDto> result = carriageService.getCarriagesPaged(search, status, pageable);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/{id}/with-seats")
    public ResponseEntity<CarriageWithSeatsDto> getCarriageWithSeats(@PathVariable Integer id) {
        CarriageDto carriage = carriageService.getCarriageById(id);
        List<SeatDto> seats = seatService.getSeatsByCarriageId(id);
        return ResponseEntity.ok(new CarriageWithSeatsDto(carriage, seats));
    }
    @GetMapping("/with-seats")
    public ResponseEntity<List<CarriageWithSeatsDto>> getAllCarriagesWithSeats() {
        List<CarriageWithSeatsDto> result = carriageService.getAllCarriagesWithSeats();
        return ResponseEntity.ok(result);
    }

    // Helper method để xử lý sort param
    private Sort parseSort(String[] sort) {
        if (sort.length == 2) {
            return Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        } else {
            return Sort.by("createdAt").descending();
        }
    }
}
