package com.example.betickettrain.service;

import com.example.betickettrain.dto.BookingDto;
import com.example.betickettrain.dto.TicketDto;
import com.example.betickettrain.entity.Booking;
import com.example.betickettrain.entity.RefundPolicy;
import com.example.betickettrain.entity.Ticket;
import com.example.betickettrain.entity.Trip;
import jakarta.mail.MessagingException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.List;

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

    @Async("emailExecutor")
    @Retryable(
            value = {MailException.class, MessagingException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    void sendBookingCancelEmail(String to, Booking booking,String tripCode, String reason);

    void sendRefundRequestedEmail(Booking booking, List<Ticket> tickets, double refundAmount, RefundPolicy policy);
    @Async("emailExecutor")
    @Retryable(
            value = {MailException.class, MessagingException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    void sendRefundApprovedEmail(Booking booking, List<TicketDto> tickets, double refundAmount);

    @Async("emailExecutor")
    @Retryable(
            value = {MailException.class, MessagingException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    void sendRefundRejectedEmail(Booking booking, List<TicketDto> tickets, String reason);

}
