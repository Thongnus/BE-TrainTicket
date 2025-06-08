package com.example.betickettrain.repository;

import com.example.betickettrain.dto.MonthlyStatisticsProjection;
import com.example.betickettrain.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticsRepository extends JpaRepository<Booking, Integer> {
    @Query(value = "CALL TrainTicketSystem_Statistics()", nativeQuery = true)
    MonthlyStatisticsProjection getMonthlyStatistics();
}
