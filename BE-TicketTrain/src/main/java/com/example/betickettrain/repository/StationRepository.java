package com.example.betickettrain.repository;

import com.example.betickettrain.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Integer> {
    List<Station> findByStatus(Station.Status status);
    List<Station> findByCityContainingIgnoreCase(String city);
    List<Station> findByProvinceContainingIgnoreCase(String province);
    List<Station> findByStationNameContainingIgnoreCase(String stationName);
    boolean existsByStationNameIgnoreCase(String stationName);

}