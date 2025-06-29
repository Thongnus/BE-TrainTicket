package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.dto.CarriageDto;
import com.example.betickettrain.dto.CarriageWithSeatsDto;
import com.example.betickettrain.dto.SeatDto;
import com.example.betickettrain.entity.Carriage;
import com.example.betickettrain.entity.Train;
import com.example.betickettrain.entity.Trip;
import com.example.betickettrain.exceptions.ErrorCode;
import com.example.betickettrain.mapper.CarriageMapper;
import com.example.betickettrain.mapper.SeatMapper;
import com.example.betickettrain.repository.CarriageRepository;
import com.example.betickettrain.repository.SeatRepository;
import com.example.betickettrain.repository.TrainRepository;
import com.example.betickettrain.service.CarriageService;
import com.example.betickettrain.service.GenericCacheService;
import com.example.betickettrain.util.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CarriageServiceImpl implements CarriageService {

    private static final String ALL_KEY = "all";
    private final CarriageRepository carriageRepository;
    private final CarriageMapper carriageMapper;
    private final GenericCacheService cacheService;
    private final SeatRepository seatRepository;
    private final ObjectMapper objectMapper;
    private final SeatMapper seatMapper;
    private final TrainRepository trainRepository;

    @Override
    public CarriageDto createCarriage(CarriageDto dto) {
        if (dto.getTrainId() != null) {
            dto.setCarriageId(null);
        }
        Carriage entity = carriageMapper.toEntity(dto);
        // ✅ Kiểm tra trainId có hợp lệ không
        if (dto.getTrainId() != null) {
            Train train = trainRepository.findById(Long.valueOf(dto.getTrainId())).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Train với id = " + dto.getTrainId()));
            entity.setTrain(train);
        }
        Carriage saved = carriageRepository.save(entity);
        cacheService.clearCache(Constants.Cache.CACHE_CARRIAGE);
        //   cacheService.remove(Constants.Cache.CACHE_CARRIAGE, ALL_KEY);
        cacheService.remove(Constants.Cache.CACHE_CARRIAGE_WITH_SEATS, ALL_KEY);

        return carriageMapper.toDto(saved);
    }

    @Override
    public CarriageDto updateCarriage(Integer id, CarriageDto dto) {
        Carriage updated = carriageRepository.findById(id).map(existing -> {
            // Cập nhật các field thường bằng MapStruct
            carriageMapper.partialUpdate(dto, existing);

            // ✅ Cập nhật train nếu trainId khác và hợp lệ
            if (dto.getTrainId() != null) {
                Integer currentTrainId = existing.getTrain() != null ? existing.getTrain().getTrainId() : null;
                if (!dto.getTrainId().equals(currentTrainId)) {
                    Train train = trainRepository.findById(Long.valueOf(dto.getTrainId())).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Train với id = " + dto.getTrainId()));
                    existing.setTrain(train);
                }
            }

            return carriageRepository.save(existing);
        }).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CARRIAGE_NOT_FOUND.message + id));

        // ✅ Xoá cache liên quan
        cacheService.remove(Constants.Cache.CACHE_CARRIAGE, id);
        cacheService.remove(Constants.Cache.CACHE_CARRIAGE, ALL_KEY);
        cacheService.remove(Constants.Cache.CACHE_CARRIAGE_WITH_SEATS, ALL_KEY);

        return carriageMapper.toDto(updated);
    }


    @Transactional
    @Override
    public void deleteCarriage(Integer id) {
        if (!carriageRepository.existsById(id)) {
            throw new RuntimeException(ErrorCode.CARRIAGE_NOT_FOUND.message + id);
        }
        seatRepository.deleteAllByCarriage_CarriageId(id); // ✅ Xoá tất cả ghế liên quan đến Carriage
        carriageRepository.deleteById(id);

        // ❌ Xoá cả cache liên quan đến CarriageWithSeats
        cacheService.remove(Constants.Cache.CACHE_CARRIAGE, id);
        cacheService.remove(Constants.Cache.CACHE_CARRIAGE, ALL_KEY);
        cacheService.remove(Constants.Cache.CACHE_CARRIAGE_WITH_SEATS, ALL_KEY); // ✅ thêm dòng này
    }

    @Override
    public CarriageDto getCarriageById(Integer id) {
        CarriageDto cached = cacheService.get(Constants.Cache.CACHE_CARRIAGE, id, CarriageDto.class);
        if (cached != null) return cached;

        CarriageDto dto = carriageRepository.findById(id).map(carriageMapper::toDto).orElseThrow(() -> new RuntimeException(ErrorCode.CARRIAGE_NOT_FOUND.message + id));

        cacheService.put(Constants.Cache.CACHE_CARRIAGE, id, dto);
        return dto;
    }

    @Override
    public List<CarriageDto> getAllCarriages() {
        Object raw = cacheService.get(Constants.Cache.CACHE_CARRIAGE, ALL_KEY);
        if (raw != null) {
            // deserialize đúng kiểu
            List<CarriageDto> cached = objectMapper.convertValue(raw, new TypeReference<List<CarriageDto>>() {
            });
            return cached;
        }

        List<CarriageDto> dtos = carriageRepository.findAll().stream().map(carriageMapper::toDto).toList();

        cacheService.put(Constants.Cache.CACHE_CARRIAGE, ALL_KEY, dtos);
        return dtos;
    }


    @Override
    public Integer countCarriageActive(String status) {
        return carriageRepository.countCarriageByStatus(status);
    }

    @Override
    public Page<CarriageDto> getCarriagesPaged(String search, String status, Pageable pageable) {
        Specification<Carriage> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";


                Join<Object, Object> trainJoin = root.join("train");

                predicates.add(cb.or(cb.like(cb.lower(root.get("tripCode")), searchPattern), cb.like(cb.lower(trainJoin.get("trainName")), searchPattern)));
            }

            if (status != null && !status.equalsIgnoreCase("all")) {
                try {
                    Trip.Status tripStatus = Trip.Status.valueOf(status.toLowerCase());
                    predicates.add(cb.equal(root.get("status"), tripStatus));
                } catch (IllegalArgumentException e) {
                    // Nếu truyền sai enum (ví dụ: "running") → bỏ lọc
                    log.warn("Trạng thái chuyến tàu không hợp lệ: " + status);
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return carriageRepository.findAll(spec, pageable).map(carriageMapper::toDto);

    }

    @Override
    public List<CarriageWithSeatsDto> getAllCarriagesWithSeats() {
        Object cachedRaw = cacheService.get(Constants.Cache.CACHE_CARRIAGE_WITH_SEATS, ALL_KEY);
        if (cachedRaw != null) {
            return objectMapper.convertValue(cachedRaw, new TypeReference<List<CarriageWithSeatsDto>>() {
            });
        }

        List<CarriageDto> carriages = getAllCarriages();

        List<CarriageWithSeatsDto> result = carriages.stream().map(carriage -> {
            List<SeatDto> seats = seatRepository.findByCarriageCarriageId(carriage.getCarriageId()).stream().map(seatMapper::toDto) // ✅ fix chỗ này
                    .collect(Collectors.toList());

            return new CarriageWithSeatsDto(carriage, seats);
        }).collect(Collectors.toList());

        // ✅ Lưu cache
        cacheService.put(Constants.Cache.CACHE_CARRIAGE_WITH_SEATS, ALL_KEY, result);
        return result;
    }
}

