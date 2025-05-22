package com.example.betickettrain.service;

import com.example.betickettrain.dto.BookingDto;
import com.example.betickettrain.dto.BookingLockRequest;

public interface BookingService {
    void lockSeats(BookingLockRequest request);
    String initiateCheckout(BookingDto request);
}
