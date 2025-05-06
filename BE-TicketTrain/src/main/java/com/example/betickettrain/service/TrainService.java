package com.example.betickettrain.service;

import com.example.betickettrain.dto.TrainDTO;
import java.util.List;

public interface TrainService {
    TrainDTO createTrain(TrainDTO trainDTO);
    TrainDTO updateTrain(Long id, TrainDTO trainDTO);
    TrainDTO getTrainById(Long id);
    List<TrainDTO> getAllTrains();
    void deleteTrain(Long id);
}