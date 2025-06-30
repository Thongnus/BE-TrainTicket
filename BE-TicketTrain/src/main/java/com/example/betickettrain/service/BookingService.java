package com.example.betickettrain.service;

import com.example.betickettrain.dto.*;
import com.example.betickettrain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
public interface BookingService {
    void lockSeats(BookingLockRequest request);

    String initiateCheckout(BookingCheckoutRequest request, User user);


    boolean handleVnPayCallback(String bookingCode, String responseCode);

    Page<BookingDto> findBookings(String search, String bookingStatus, String paymentStatus, Pageable pageable);

    List<BookingHistoryDTO>  getBookingHistorybyUser(Long userId);

    BookingDto findBookingByBookingCode(String bookingCode);

    void markTicketsCheckedIn(Integer bookingId);

    boolean cancelBookingByAdmin(Integer bookingId);
}
