package com.example.betickettrain.repository;

import com.example.betickettrain.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Integer> {
}