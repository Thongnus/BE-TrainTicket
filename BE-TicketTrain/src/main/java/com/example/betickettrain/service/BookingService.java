package com.example.betickettrain.service;

import com.example.betickettrain.dto.BookingCheckoutRequest;
import com.example.betickettrain.dto.BookingDto;
import com.example.betickettrain.dto.BookingLockRequest;
import com.example.betickettrain.entity.User;

public interface BookingService {
    void lockSeats(BookingLockRequest request);

    String initiateCheckout(BookingCheckoutRequest request, User user);

    boolean handleVnPayCallback(String bookingCode, String responseCode);
}
