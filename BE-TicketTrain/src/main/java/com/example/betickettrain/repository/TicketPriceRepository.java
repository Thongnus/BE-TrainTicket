package com.example.betickettrain.repository;

import com.example.betickettrain.entity.Carriage;
import com.example.betickettrain.entity.TicketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TicketPriceRepository extends JpaRepository<TicketPrice, Integer> {
    List<TicketPrice> findByRouteRouteId(Integer routeRouteId);

    @Query("SELECT tp FROM TicketPrice tp WHERE tp.route.routeId = :routeId " +
            "AND tp.carriageType In  :carriageType " +
            "AND :departureDate BETWEEN tp.startDate AND tp.endDate")
    List<TicketPrice> findByRouteAndCarriageTypeAndDateRange(
            @Param("routeId") Integer routeId,
            @Param("carriageType") List<Carriage.CarriageType> carriageType,
            @Param("departureDate") LocalDate departureDate
    );


}