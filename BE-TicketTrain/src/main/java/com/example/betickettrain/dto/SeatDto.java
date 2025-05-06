package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Seat;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Seat}
 */
@Value
public class SeatDto implements Serializable {
    Integer seatId;
    String seatNumber;
    Seat.SeatType seatType;
    Seat.Status status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}