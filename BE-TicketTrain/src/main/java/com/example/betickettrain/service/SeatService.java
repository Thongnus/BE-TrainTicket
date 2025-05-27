package com.example.betickettrain.service;

import com.example.betickettrain.dto.CarriageSeatDto;
import com.example.betickettrain.dto.SeatDto;

import java.util.List;
public interface SeatService {
        List<CarriageSeatDto> getCarriagesWithSeats(Integer tripId);

        SeatDto createSeat(SeatDto seatDto);

        SeatDto updateSeat(Integer id, SeatDto seatDto);

        SeatDto getSeat(Integer id);

        List<SeatDto> getAllSeats();

        void deleteSeat(Integer id);

        void unLockSeat(Integer tripId, List<Integer> idSeat);
}
