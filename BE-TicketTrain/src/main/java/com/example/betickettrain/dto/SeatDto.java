package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Seat;
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
@NoArgsConstructor // ✅ BẮT BUỘC CHO JACKSON
@AllArgsConstructor
public class SeatDto implements Serializable {
    Integer seatId;
    String seatNumber;
    Seat.SeatType seatType;
    Seat.Status status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Double price;
}