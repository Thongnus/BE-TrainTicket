package com.example.betickettrain.repository;

import com.example.betickettrain.entity.RouteStation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface RouteStationRepository extends JpaRepository<RouteStation, Integer> {
    List<RouteStation> findByRouteRouteIdOrderByStopOrderAsc(Integer routeRouteId);

    void deleteByRoute_RouteId(Integer routeRouteId);
}