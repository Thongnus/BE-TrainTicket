package com.example.betickettrain.repository;

import com.example.betickettrain.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Integer> {
   boolean   existsByOriginStation_StationIdAndDestinationStation_StationId(
           Integer originStationId, Integer destinationStationId);
}