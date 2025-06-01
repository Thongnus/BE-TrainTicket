package com.example.betickettrain.service;

import com.example.betickettrain.dto.BookingCheckoutRequest;
import com.example.betickettrain.dto.BookingDto;
import com.example.betickettrain.dto.BookingLockRequest;
import com.example.betickettrain.dto.UserDto;
import com.example.betickettrain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    void lockSeats(BookingLockRequest request);

    String initiateCheckout(BookingCheckoutRequest request, User user);

    boolean handleVnPayCallback(String bookingCode, String responseCode);

    Page<BookingDto> findBookings(String search, String bookingStatus, String paymentStatus, Pageable pageable);
}
