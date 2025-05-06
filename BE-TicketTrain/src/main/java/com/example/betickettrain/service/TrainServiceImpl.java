//package com.example.betickettrain.service.impl;
//
//import com.example.betickettrain.constant.TrainConstants;
//import com.example.betickettrain.dto.TrainDTO;
//import com.example.betickettrain.entity.Train;
//import com.example.betickettrain.mapper.TrainMapper;
//import com.example.betickettrain.repository.TrainRepository;
//import com.example.betickettrain.service.TrainService;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class TrainServiceImpl implements TrainService {
//
//    private final TrainRepository trainRepository;
//    private final TrainMapper trainMapper;
//
//    @Override
//    @Transactional
//    public TrainDTO createTrain(TrainDTO trainDTO) {
//        Train train = trainMapper.toEntity(trainDTO);
//        Train savedTrain = trainRepository.save(train);
//        return trainMapper.toDto(savedTrain);
//    }
//
//    @Override
//    @Transactional
//    public TrainDTO updateTrain(Long id, TrainDTO trainDTO) {
//        Train existingTrain = trainRepository.findById(id)
//            .orElseThrow(() -> new EntityNotFoundException(TrainConstants.TRAIN_NOT_FOUND + id));
//
//        Train train = trainMapper.toEntity(trainDTO);
//        train.setId(id);
//        Train updatedTrain = trainRepository.save(train);
//        return trainMapper.toDTO(updatedTrain);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public TrainDTO getTrainById(Long id) {
//        Train train = trainRepository.findById(id)
//            .orElseThrow(() -> new EntityNotFoundException(TrainConstants.TRAIN_NOT_FOUND + id));
//        return trainMapper.toDTO(train);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<TrainDTO> getAllTrains() {
//        return trainRepository.findAll().stream()
//            .map(trainMapper::toDTO)
//            .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional
//    public void deleteTrain(Long id) {
//        if (!trainRepository.existsById(id)) {
//            throw new EntityNotFoundException(TrainConstants.TRAIN_NOT_FOUND + id);
//        }
//        trainRepository.deleteById(id);
//    }
//}