package com.example.betickettrain.service;

import com.example.betickettrain.dto.TrainDto;
import com.example.betickettrain.entity.Train;
import org.springframework.stereotype.Service;

import java.util.List;

public interface TrainService {
    TrainDto createTrain(TrainDto trainDto);
    TrainDto updateTrain(Long id, TrainDto trainDTO);
    TrainDto getTrainById(Long id);
    List<TrainDto> getAllTrains();
    void deleteTrain(Long id);

    List<TrainDto> getTrainsByStatus(Train.Status status);
}