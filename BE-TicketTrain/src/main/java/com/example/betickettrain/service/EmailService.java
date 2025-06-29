package com.example.betickettrain.service;

import com.example.betickettrain.dto.BookingDto;
import com.example.betickettrain.entity.Booking;
import com.example.betickettrain.entity.Trip;
import jakarta.mail.MessagingException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;

public interface EmailService {
    void sendEmail(String to, String subject, String body) throws MessagingException;

    // Tạo notification khi email thành công (optional - để user biết đã gửi email)



    void createSuccessNotification(BookingDto booking);

    // Tạo notification backup khi email fail
    void createEmailFailureNotification(BookingDto booking);

    // Tạo notification cho trường hợp fail hoàn toàn

    void createPermanentFailureNotification(BookingDto booking);

    void notifyAdminEmailFailure(BookingDto booking);

    void sendEmailWithQRCode( String to, String subject, String text, byte[] qrCodeBytes) throws MessagingException;

//    void sendTripDelayEmail(String to, String tripCode, String departureTime, int delayMinutes, String reason);
//
//    void sendTripCancelEmail(String to, String tripCode, String departureTime, String reason);


    void sendTripStatusEmail(String to, String tripCode, LocalDateTime departureTime, Trip.Status status, Integer delayMinutes, String reason);
}
