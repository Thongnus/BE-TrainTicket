package com.example.betickettrain.service;

import com.example.betickettrain.entity.Booking;
import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;

public interface EmailService {
    void sendEmail(String to, String subject, String body) throws MessagingException;

    // Tạo notification khi email thành công (optional - để user biết đã gửi email)



    void createSuccessNotification(Booking booking);

    // Tạo notification backup khi email fail
    void createEmailFailureNotification(Booking booking);

    // Tạo notification cho trường hợp fail hoàn toàn

    void createPermanentFailureNotification(Booking booking);

    void notifyAdminEmailFailure(Booking booking);
}
