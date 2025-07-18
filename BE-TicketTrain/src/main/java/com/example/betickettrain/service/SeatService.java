package com.example.betickettrain.service;

import com.example.betickettrain.dto.CarriageSeatDto;
import com.example.betickettrain.dto.SeatDto;
import com.example.betickettrain.dto.TripWithSeatsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
public interface SeatService {
        List<TripWithSeatsDto> getCarriagesWithSeats(Integer tripId);

        SeatDto createSeat(SeatDto seatDto);

        SeatDto updateSeat(Integer id, SeatDto seatDto);

        SeatDto getSeat(Integer id);

        List<SeatDto> getAllSeats();

        void deleteSeat(Integer id);

        void unLockSeat(Integer tripId, List<Integer> idSeat);
        List<CarriageSeatDto> getCarriageSeatByTripId(Integer tripId);
    Page<SeatDto> getPagedSeats(String search,Pageable pageable);

        List<SeatDto> getSeatsByCarriageId(Integer id);
}
