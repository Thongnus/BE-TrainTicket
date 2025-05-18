package com.example.betickettrain.controller;

import com.example.betickettrain.dto.TrainDto;
import com.example.betickettrain.service.TrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trains")
@RequiredArgsConstructor
public class TrainController {

    private final TrainService trainService;

    // 🔹 GET: Lấy tất cả tàu
    @GetMapping
    public ResponseEntity<List<TrainDto>> getAllTrains() {
        return ResponseEntity.ok(trainService.getAllTrains());
    }

    // 🔹 GET: Lấy tàu theo ID
    @GetMapping("/{id}")
    public ResponseEntity<TrainDto> getTrainById(@PathVariable Long id) {
        return ResponseEntity.ok(trainService.getTrainById(id));
    }

    // 🔹 POST: Tạo tàu mới
    @PostMapping
    public ResponseEntity<TrainDto> createTrain(@RequestBody TrainDto trainDTO) {
        return ResponseEntity.ok(trainService.createTrain(trainDTO));
    }

    // 🔹 PUT: Cập nhật tàu
    @PutMapping("/{id}")
    public ResponseEntity<TrainDto> updateTrain(@PathVariable Long id, @RequestBody TrainDto trainDTO) {
        return ResponseEntity.ok(trainService.updateTrain(id, trainDTO));
    }

    // 🔹 DELETE: Xóa tàu
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrain(@PathVariable Long id) {
        trainService.deleteTrain(id);
        return ResponseEntity.noContent().build();
    }
}
