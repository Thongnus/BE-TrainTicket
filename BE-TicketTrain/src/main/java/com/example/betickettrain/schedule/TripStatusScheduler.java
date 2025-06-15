package com.example.betickettrain.schedule;

import com.example.betickettrain.repository.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TripStatusScheduler {

    private final TripRepository tripRepository;

    @Scheduled(cron = "0 0 1 * * *") // chạy lúc 1h sáng mỗi ngày
    @Transactional
    public void autoCompleteTrips() {
        int updated = tripRepository.markTripsCompletedIfExpired(LocalDateTime.now().minusDays(1));
        log.info("✅ Đã tự động cập nhật {} chuyến sang trạng thái COMPLETED", updated);
    }
}
