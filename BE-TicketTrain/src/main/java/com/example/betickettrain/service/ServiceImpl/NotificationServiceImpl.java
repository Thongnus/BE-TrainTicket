package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.dto.BookingDto;
import com.example.betickettrain.entity.Booking;
import com.example.betickettrain.entity.Trip;
import com.example.betickettrain.service.EmailService;
import com.example.betickettrain.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private EmailService emailService;

    @Override
    public void notifyUsers(Trip trip, List<String> userEmails) {

        for (String email : userEmails) {
            emailService.sendTripStatusEmail(email, trip.getTripCode(), trip.getDepartureTime(), trip.getStatus(), trip.getDelayMinutes(), trip.getDelayReason());
        }
    }

    @Override
    public void notifyBookingCancellation(String to, Booking booking, String tripCode, String reason) {
         emailService.sendBookingCancelEmail(to, booking, tripCode, reason);
    }


}
