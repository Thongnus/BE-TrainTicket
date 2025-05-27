package com.example.betickettrain.repository;

import com.example.betickettrain.entity.Trip;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Integer> {
    boolean existsByTripCode(String tripCode);

    @Query( nativeQuery = true,value = "select * from Trips t where DATE(departure_time) = :date and t.status")
    Trip findTripByDepartureTimeAndStatus(LocalDateTime departureTime, String status);

    @Procedure(procedureName = "find_trip_by_trip_code")
    Trip findTripByTripCode(String tripCode);

    @Query("SELECT t FROM Trip t " +
            "JOIN FETCH t.route r " +
            "JOIN FETCH r.originStation " +
            "JOIN FETCH r.destinationStation " +
            "JOIN FETCH t.train " +
            "WHERE t.tripId = :tripId")
    Optional<Trip> findByIdWithRouteAndStations(@Param("tripId") Integer tripId);
}