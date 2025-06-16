package com.example.betickettrain.controller;

import com.example.betickettrain.dto.StationDto;
import com.example.betickettrain.entity.Station;
import com.example.betickettrain.service.StationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @GetMapping
    public ResponseEntity<List<StationDto>> getAllStations() {
        return ResponseEntity.ok(stationService.getAllStations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationDto> getStationById(@PathVariable Integer id) {
        return ResponseEntity.ok(stationService.getStationById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<StationDto>> getStationsByStatus(@PathVariable Station.Status status) {
        return ResponseEntity.ok(stationService.getStationsByStatus(status));
    }

    @GetMapping("/search")
    public ResponseEntity<List<StationDto>> searchStations(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(stationService.searchStations(keyword));
    }

    @PostMapping
    public ResponseEntity<StationDto> createStation(@Valid @RequestBody StationDto stationDto) {
        return new ResponseEntity<>(stationService.createStation(stationDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StationDto> updateStation(@PathVariable Integer id, @Valid @RequestBody StationDto stationDto) {
        return ResponseEntity.ok(stationService.updateStation(id, stationDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Integer id) {
        stationService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/paged")
    public Page<StationDto> getPagedStations(@RequestParam(defaultValue = "0")  int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return stationService.getStationsPaged(page, size);
    }

}