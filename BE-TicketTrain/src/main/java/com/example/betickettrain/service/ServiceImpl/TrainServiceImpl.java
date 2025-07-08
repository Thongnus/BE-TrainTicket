package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.anotation.LogAction;
import com.example.betickettrain.anotation.SystemLogAspect;
import com.example.betickettrain.dto.TrainDto;
import com.example.betickettrain.entity.SystemLog;
import com.example.betickettrain.entity.Train;
import com.example.betickettrain.entity.User;
import com.example.betickettrain.mapper.TrainMapper;
import com.example.betickettrain.repository.SystemLogRepository;
import com.example.betickettrain.repository.TrainRepository;
import com.example.betickettrain.service.GenericCacheService;
import com.example.betickettrain.service.SystemLogService;
import com.example.betickettrain.service.TrainService;
import com.example.betickettrain.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainServiceImpl implements TrainService {

    private final TrainRepository trainRepository;
    private final TrainMapper trainMapper;
    private final GenericCacheService cacheService;
    private final SystemLogRepository systemLogRepository;
    private final SystemLogService systemLogService;
    private static final String ALL_TRAINS_KEY = "all";
    @LogAction(action = Constants.Action.CREATE,entity = "Train", description = "Create a new Train")
    @Override
    public TrainDto createTrain(TrainDto trainDTO) {
        Train train = trainMapper.toEntity(trainDTO);
        Train saved = trainRepository.save(train);
        cacheService.clearCache(Constants.Cache.CACHE_TRAIN);
        return trainMapper.toDto(saved);
    }
    @LogAction(action = Constants.Action.UPDATE,entity = "Train", description = "Update a new Train")
    @Override
    public TrainDto updateTrain(Long id, TrainDto trainDTO) {
        TrainDto updated = trainRepository.findById(id)
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
    public TrainDto getTrainById(Long id) {
        TrainDto cached = (TrainDto)cacheService.get(Constants.Cache.CACHE_TRAIN, id,TrainDto.class);
        if (cached != null) return cached;
        log.info(" ️️Lấy thông tin train từ DB với id"+id);
        TrainDto dto = trainRepository.findById(id)
                .map(trainMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Train not found with id: " + id));

        cacheService.put(Constants.Cache.CACHE_TRAIN, id, dto);
        return dto;
    }

    @Override

    public List<TrainDto> getAllTrains() {
        List<TrainDto> cached = cacheService.get(Constants.Cache.CACHE_TRAIN, ALL_TRAINS_KEY);
        if (cached != null) return cached;
        log.info(" ️️Lấy thông tin train từ DB");
        List<Train> trains = trainRepository.findAll();
        List<TrainDto> dtos = trains.stream()
                .map(trainMapper::toDto)
                .toList();

        cacheService.put(Constants.Cache.CACHE_TRAIN, ALL_TRAINS_KEY, dtos);
        return dtos;
    }

    @Override
    @LogAction(action = Constants.Action.DELETE,entity = "Train", description = "Delete a train")
    public void deleteTrain(Long id) {
        trainRepository.deleteById(id);
        cacheService.remove(Constants.Cache.CACHE_TRAIN, id);
        cacheService.remove(Constants.Cache.CACHE_TRAIN, ALL_TRAINS_KEY);
        Long userId= SystemLogAspect.getCurrentUserId();
       SystemLog logg = SystemLog.builder()
                .user(userId != null ? User.builder().userId(userId).build() : null)
                .action(Constants.Action.DELETE)
                .entityType("Train")
                .entityId(Math.toIntExact(id))
                .description("Delete a train with ID: " + id)
              //  .ipAddress(request.getRemoteAddr())
              //  .userAgent(request.getHeader("User-Agent"))
                //   .logTime(LocalDateTime.now())
                .build();

//        systemLogRepository.save(logg);
       // log.debug("📘 Logged [{}] [{}:{}] - {}", logAction.action(), logAction.entity(), entityId, desString);
        systemLogService.logAction(logg);
        log.debug("📘 Sent log to WebSocket: {}", logg);
    }
    @Override
    public List<TrainDto> getTrainsByStatus(Train.Status status) {
        List<Train> trains = trainRepository.findAllByStatus(status);
        return trains.stream()
                .map(trainMapper::toDto)
                .toList();
    }
}
