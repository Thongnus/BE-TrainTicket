package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.dto.CarriageDto;
import com.example.betickettrain.entity.Carriage;
import com.example.betickettrain.exceptions.ErrorCode;
import com.example.betickettrain.mapper.CarriageMapper;
import com.example.betickettrain.repository.CarriageRepository;
import com.example.betickettrain.service.CarriageService;
import com.example.betickettrain.service.GenericCacheService;
import com.example.betickettrain.util.Constants;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
public class CarriageServiceImpl implements CarriageService {

    private final CarriageRepository carriageRepository;
    private final CarriageMapper carriageMapper;
    private final GenericCacheService cacheService;

    private static final String ALL_KEY = "all";

    @Override
    public CarriageDto createCarriage(CarriageDto dto) {
        Carriage entity = carriageMapper.toEntity(dto);
        Carriage saved = carriageRepository.save(entity);
        cacheService.clearCache(Constants.Cache.CACHE_CARRIAGE);
        return carriageMapper.toDto(saved);
    }

    @Override
    public CarriageDto updateCarriage(Integer id, CarriageDto dto) {
        Carriage updated = carriageRepository.findById(id)
                .map(existing -> {
                    carriageMapper.partialUpdate(dto, existing);
                    return carriageRepository.save(existing);
                })
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CARRIAGE_NOT_FOUND.message + id ));

        cacheService.remove(Constants.Cache.CACHE_CARRIAGE, id);
        cacheService.remove(Constants.Cache.CACHE_CARRIAGE, ALL_KEY);
        return carriageMapper.toDto(updated);
    }

    @Override
    public void deleteCarriage(Integer id) {
        if (!carriageRepository.existsById(id)) {
            throw new RuntimeException(ErrorCode.CARRIAGE_NOT_FOUND.message + id);
        }
        carriageRepository.deleteById(id);
        cacheService.remove(Constants.Cache.CACHE_CARRIAGE, id);
        cacheService.remove(Constants.Cache.CACHE_CARRIAGE, ALL_KEY);
    }

    @Override
    public CarriageDto getCarriageById(Integer id) {
        CarriageDto cached = cacheService.get(Constants.Cache.CACHE_CARRIAGE, id, CarriageDto.class);
        if (cached != null) return cached;

        CarriageDto dto = carriageRepository.findById(id)
                .map(carriageMapper::toDto)
                .orElseThrow(() -> new RuntimeException(ErrorCode.CARRIAGE_NOT_FOUND.message + id));

        cacheService.put(Constants.Cache.CACHE_CARRIAGE, id, dto);
        return dto;
    }

    @Override
    public List<CarriageDto> getAllCarriages() {
        List<CarriageDto> cached = cacheService.get(Constants.Cache.CACHE_CARRIAGE, ALL_KEY);
        if (cached != null) return cached;

        List<CarriageDto> dtos = carriageRepository.findAll().stream()
                .map(carriageMapper::toDto)
                .toList();

        cacheService.put(Constants.Cache.CACHE_CARRIAGE, ALL_KEY, dtos);
        return dtos;
    }

    @Override
    public Integer countCarriageActive( String status) {
        return carriageRepository.countCarriageByStatus(status);
    }
}

