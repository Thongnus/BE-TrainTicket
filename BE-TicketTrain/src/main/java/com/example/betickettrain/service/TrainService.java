package com.example.betickettrain.service;

import com.example.betickettrain.dto.TrainDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface TrainService {
    TrainDto createTrain(TrainDto trainDto);
    TrainDto updateTrain(Long id, TrainDto trainDTO);
    TrainDto getTrainById(Long id);
    List<TrainDto> getAllTrains();
    void deleteTrain(Long id);
}