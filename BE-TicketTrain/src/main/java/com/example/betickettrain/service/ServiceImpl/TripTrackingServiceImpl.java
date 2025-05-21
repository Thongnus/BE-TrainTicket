package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.dto.TripTrackingDto;
import com.example.betickettrain.entity.TripSchedule;
import com.example.betickettrain.repository.TripScheduleRepository;
import com.example.betickettrain.service.TripTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class TripTrackingServiceImpl implements TripTrackingService {

    private final TripScheduleRepository tripScheduleRepository;

    @Override
    public TripTrackingDto getTripTracking(Integer tripId) {
        List<TripSchedule> schedules = tripScheduleRepository.findByTripTripIdOrderByScheduledArrivalAsc(tripId);

        LocalDateTime now = LocalDateTime.now();
        TripSchedule current = null, next = null;

        for (int i = 0; i < schedules.size(); i++) {
            TripSchedule s = schedules.get(i);
            if (s.getActualArrival() != null && s.getActualArrival().isBefore(now)) {
                current = s;
            } else if (s.getScheduledArrival() != null && s.getScheduledArrival().isAfter(now)) {
                next = s;
                break;
            }
        }

        return getTripTrackingDto(current, next);
    }

    @Override
    @Transactional
    public void markStationArrived(Integer tripId, Integer stationId, LocalDateTime actualArrival) {
        TripSchedule schedule = tripScheduleRepository.findByTrip(tripId, stationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch trình cho ga này"));

        schedule.setActualArrival(actualArrival);
        schedule.setStatus(TripSchedule.Status.arrived);

        tripScheduleRepository.save(schedule);
    }

    @Override
    public void markStationDeparted(Integer tripId, Integer stationId, LocalDateTime actualDeparture) {
        TripSchedule schedule = tripScheduleRepository.findByTripTripIdAndStationStationId(tripId, stationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch trình cho ga này"));

        schedule.setActualDeparture(actualDeparture);
        schedule.setStatus(TripSchedule.Status.departed);

        tripScheduleRepository.save(schedule);
    }

    private static TripTrackingDto getTripTrackingDto(TripSchedule current, TripSchedule next) {
        TripTrackingDto dto = new TripTrackingDto();
        if (current != null) {
            dto.setCurrentStation(current.getStation().getStationName());
            dto.setScheduledArrival(current.getScheduledArrival().toString());
            dto.setActualArrival(current.getActualArrival() != null ? current.getActualArrival().toString() : null);
            dto.setStatus(current.getStatus().name().toLowerCase());
        }
        if (next != null) {
            dto.setNextStation(next.getStation().getStationName());
            dto.setEstimatedNextArrival(next.getScheduledArrival().toString());
        }
        return dto;
    }
}
