package com.example.betickettrain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import  java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingLockRequest {
    private Integer tripId;
    private List<Integer> seatIds;
}