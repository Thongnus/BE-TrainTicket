package com.example.betickettrain.service;

import com.example.betickettrain.dto.BookingDto;
import com.example.betickettrain.entity.Booking;
import com.example.betickettrain.entity.Trip;

import java.util.List;

public interface NotificationService {
    void notifyUsers(Trip trip, List<String> userEmails);
   void  notifyBookingCancellation(String to, Booking booking, String tripCode, String reason);

//    void notifyBookingSuccess(String bookingCode, String userEmail);
//
//    void notifyBookingFailure(String bookingCode, String userEmail, String reason);
//
//
//    void notifyBookingCheckIn(String bookingCode, String userEmail);

}
