package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Seat;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Seat}
 */
@Data
@NoArgsConstructor //
@AllArgsConstructor
public class SeatDto   {
    Integer seatId;
    String seatNumber;
    Seat.SeatType seatType;
    Seat.Status status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
    Double price;
    boolean booked; // true if already booked
    private CarriageDto carriage;
}