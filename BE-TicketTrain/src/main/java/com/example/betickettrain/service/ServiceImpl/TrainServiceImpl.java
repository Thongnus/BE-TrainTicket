package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.dto.TrainDTO;
import com.example.betickettrain.entity.Train;
import com.example.betickettrain.mapper.TrainMapper;
import com.example.betickettrain.repository.TrainRepository;
import com.example.betickettrain.service.GenericCacheService;
import com.example.betickettrain.service.TrainService;
import com.example.betickettrain.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {

    private final TrainRepository trainRepository;
    private final TrainMapper trainMapper;
    private final GenericCacheService cacheService;

    private static final String ALL_TRAINS_KEY = "all";

    @Override
    public TrainDTO createTrain(TrainDTO trainDTO) {
        Train train = trainMapper.toEntity(trainDTO);
        Train saved = trainRepository.save(train);
        cacheService.clearCache(Constants.Cache.CACHE_TRAIN);
        return trainMapper.toDto(saved);
    }

    @Override
    public TrainDTO updateTrain(Long id, TrainDTO trainDTO) {
        TrainDTO updated = trainRepository.findById(id)
                .map(existing -> {
                    Train entity = trainMapper.partialUpdate(trainDTO, existing);
                    return trainMapper.toDto(trainRepository.save(entity));
                })
                .orElseThrow(() -> new RuntimeException("Train not found with id: " + id));
        cacheService.remove(Constants.Cache.CACHE_TRAIN, id);
        cacheService.remove(Constants.Cache.CACHE_TRAIN, ALL_TRAINS_KEY);
        return updated;
    }

    @Override
    public TrainDTO getTrainById(Long id) {
        TrainDTO cached = cacheService.get(Constants.Cache.CACHE_TRAIN, id);
        if (cached != null) return cached;

        TrainDTO dto = trainRepository.findById(id)
                .map(trainMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Train not found with id: " + id));

        cacheService.put(Constants.Cache.CACHE_TRAIN, id, dto);
        return dto;
    }

    @Override
    public List<TrainDTO> getAllTrains() {
        List<TrainDTO> cached = cacheService.get(Constants.Cache.CACHE_TRAIN, ALL_TRAINS_KEY);
        if (cached != null) return cached;

        List<Train> trains = trainRepository.findAll();
        List<TrainDTO> dtos = trains.stream()
                .map(trainMapper::toDto)
                .toList();

        cacheService.put(Constants.Cache.CACHE_TRAIN, ALL_TRAINS_KEY, dtos);
        return dtos;
    }

    @Override
    public void deleteTrain(Long id) {
        trainRepository.deleteById(id);
        cacheService.remove(Constants.Cache.CACHE_TRAIN, id);
        cacheService.remove(Constants.Cache.CACHE_TRAIN, ALL_TRAINS_KEY);
    }
}
