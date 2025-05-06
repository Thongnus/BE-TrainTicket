package com.example.betickettrain.repository;

import com.example.betickettrain.entity.TripSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripScheduleRepository extends JpaRepository<TripSchedule, Integer> {
}