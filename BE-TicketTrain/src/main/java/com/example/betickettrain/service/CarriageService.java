package com.example.betickettrain.service;

import com.example.betickettrain.dto.CarriageDto;
import com.example.betickettrain.dto.CarriageWithSeatsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
public interface CarriageService {
  CarriageDto createCarriage(CarriageDto dto);
  CarriageDto updateCarriage(Integer id, CarriageDto dto);
  void deleteCarriage(Integer id);
  CarriageDto getCarriageById(Integer id);
  List<CarriageDto> getAllCarriages();
  Integer countCarriageActive(String status);
  Page<CarriageDto> getCarriagesPaged(String search, String status,Pageable pageable);

  List<CarriageWithSeatsDto> getAllCarriagesWithSeats();
}
