package com.example.betickettrain.repository;

import com.example.betickettrain.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import java.time.LocalDateTime;

public interface TripRepository extends JpaRepository<Trip, Integer> {
    boolean existsByTripCode(String tripCode);

    @Query( nativeQuery = true,value = "select * from Trips t where DATE(departure_time) = :date and t.status")
    Trip findTripByDepartureTimeAndStatus(LocalDateTime departureTime, String status);

    @Procedure(procedureName = "find_trip_by_trip_code")
    Trip findTripByTripCode(String tripCode);
}