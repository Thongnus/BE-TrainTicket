package com.example.betickettrain.service;

import com.example.betickettrain.dto.StationDto;
import com.example.betickettrain.entity.Station;

import java.util.List;

public interface StationService {
    List<StationDto> getAllStations();
    StationDto getStationById(Integer id);
    List<StationDto> getStationsByStatus(Station.Status status);
    List<StationDto> searchStations(String keyword);
    StationDto createStation(StationDto stationDto);
    StationDto updateStation(Integer id, StationDto stationDto);
    void deleteStation(Integer id);
}