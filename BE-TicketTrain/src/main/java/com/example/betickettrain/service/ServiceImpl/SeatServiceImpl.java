package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.anotation.LogAction;
import com.example.betickettrain.dto.CarriageSeatDto;
import com.example.betickettrain.dto.SeatDto;
import com.example.betickettrain.entity.*;
import com.example.betickettrain.mapper.SeatMapper;
import com.example.betickettrain.repository.*;
import com.example.betickettrain.service.GenericCacheService;
import com.example.betickettrain.service.SeatService;
import com.example.betickettrain.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.betickettrain.util.Constants.Cache.CACHE_SEAT;
import static com.example.betickettrain.util.Constants.Cache.CACHE_STATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {
    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;
    private final TripRepository tripRepository;
    private final CarriageRepository carriageRepository;
    private final TicketRepository ticketRepository;
    private final TicketPriceRepository ticketPriceRepository;
    private final GenericCacheService cacheService;
    private final RedisSeatLockServiceImpl redisSeatLockService;
    private static final String ALL_SEAT_KEY = "all";

    @Override
    public List<CarriageSeatDto> getCarriagesWithSeats(Integer tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        Integer trainId = trip.getTrain().getTrainId();
        Integer routeId = trip.getRoute().getRouteId();
        LocalDateTime now = LocalDateTime.now();

        Set<Integer> redisLockedSeats = redisSeatLockService.getLockedSeats(tripId);

        Map<Integer, Ticket.Status> seatStatuses = ticketRepository.findAllByTrip_TripId(tripId).stream()
                .filter(t ->
                        t.getStatus() == Ticket.Status.booked ||
                                (t.getStatus() == Ticket.Status.hold && t.getHoldExpireTime().isAfter(now))
                )
                .collect(Collectors.toMap(
                        t -> t.getSeat().getSeatId(),
                        Ticket::getStatus,
                        (a, b) -> a // nếu trùng seat, giữ 1 giá trị là đủ
                ));

        Map<Carriage.CarriageType, Double> priceMap = ticketPriceRepository.findByRouteRouteId(routeId).stream()
                .collect(Collectors.toMap(
                        TicketPrice::getCarriageType,
                        tp -> tp.getHolidaySurcharge() == null ? tp.getBasePrice() : tp.getBasePrice() + tp.getHolidaySurcharge()
                ));

        return carriageRepository.findAllByTrain_TrainId(trainId).stream().map(carriage -> {
            CarriageSeatDto dto = new CarriageSeatDto();
            dto.setCarriageId(carriage.getCarriageId());
            dto.setCarriageNumber(carriage.getCarriageNumber());
            dto.setCarriageType(String.valueOf(carriage.getCarriageType()));
            dto.setCapacity(carriage.getCapacity());

            List<SeatDto> seatDtos = seatRepository.findByCarriageCarriageId(carriage.getCarriageId()).stream().map(seat -> {
                SeatDto seatDto = new SeatDto();
                seatDto.setSeatId(seat.getSeatId());
                seatDto.setSeatNumber(seat.getSeatNumber());
                seatDto.setSeatType(seat.getSeatType());
                seatDto.setStatus(seat.getStatus());
                seatDto.setPrice(priceMap.getOrDefault(carriage.getCarriageType(), 0.0));

                boolean isRedisLocked = redisLockedSeats.contains(seat.getSeatId());
                boolean isBooked = seatStatuses.getOrDefault(seat.getSeatId(), null) == Ticket.Status.booked;
                boolean isHeld = seatStatuses.getOrDefault(seat.getSeatId(), null) == Ticket.Status.hold;

                seatDto.setBooked(isRedisLocked || isBooked || isHeld);
                return seatDto;
            }).toList();

            dto.setSeats(seatDtos);
            return dto;
        }).toList();
    }

    @LogAction(action = Constants.Action.CREATE,entity = "Seat", description = " Create a Seat")
    @Override
    public SeatDto createSeat(SeatDto seatDto) {
        Seat entity = seatMapper.toEntity(seatDto);
        Seat saved = seatRepository.save(entity);
        cacheService.clearCache(CACHE_SEAT); // xoá cache cũ
        return seatMapper.toDto(saved);
    }

    @LogAction(action = Constants.Action.UPDATE,entity = "Seat", description = " Update a Seat")
    @Override
    public SeatDto updateSeat(Integer id, SeatDto seatDto) {
        return null;
    }

    @Override
    public SeatDto getSeat(Integer id) {
        SeatDto cached = cacheService.get(CACHE_SEAT, id);
        if (cached != null) return cached;
        log.debug(" ️️Lấy thông tin ghế từ DB với id"+id);
        SeatDto dto = seatRepository.findById(id)
                .map(seatMapper::toDto)
                .orElseThrow(() -> new RuntimeException("SeatDto not found with id: " + id));

        cacheService.put(Constants.Cache.CACHE_NEWFEED, id, dto);
        return dto;
    }

    @Override
    public List<SeatDto> getAllSeats() {
        // Check cache first
        List<SeatDto> cachedStations = cacheService.get(CACHE_STATION, ALL_SEAT_KEY);

        if (cachedStations != null) {
            return cachedStations;
        }
        log.debug(" ️️Lấy thông tin ghế từ DB");
        // Cache miss - fetch from database
        List<SeatDto> seatDtos = seatRepository.findAll().stream()
                .map(seatMapper::toDto)
                .collect(Collectors.toList());

        // Save to cache
        cacheService.put(CACHE_STATION, ALL_SEAT_KEY, seatDtos);

        return seatDtos;

    }
    @LogAction(action = Constants.Action.DELETE,entity = "Seat", description = " Delete a Seat")
    @Override
    public void deleteSeat(Integer id) {
         seatRepository.deleteById(id);
        cacheService.remove(CACHE_SEAT, id);
        cacheService.remove(CACHE_SEAT, ALL_SEAT_KEY); // cập nhật lại danh sách sau khi xóa
    }
}
