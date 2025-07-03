package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.dto.BookingDto;
import com.example.betickettrain.dto.TicketDto;
import com.example.betickettrain.entity.*;
import com.example.betickettrain.mapper.BookingMapper;
import com.example.betickettrain.mapper.UserMapper;
import com.example.betickettrain.repository.FailedEmailLogRepository;
import com.example.betickettrain.repository.NotificationRepository;
import com.example.betickettrain.service.EmailService;
import com.example.betickettrain.util.TemplateMail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@EnableRetry
public class EmailServiceImpl implements EmailService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private FailedEmailLogRepository failedEmailLogRepository;
    @Autowired
    private BookingMapper bookingMapper;

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false); // false = plain text. Dùng true nếu bạn dùng HTML

            mailSender.send(message);
            log.info("✅ Email sent to {}", to);
        } catch (MessagingException e) {
            log.error("❌ Failed to send email to {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }


    @Override
    public void createSuccessNotification(BookingDto booking) {
        try {
            Notification notification = new Notification();
            notification.setUser(userMapper.toEntity(booking.getUser()));
            notification.setTitle("Xác nhận đặt vé thành công");
            notification.setMessage("Email xác nhận đặt vé " + booking.getBookingCode() + " đã được gửi đến địa chỉ email của bạn.");
            notification.setNotificationType(Notification.NotificationType.booking);
            notification.setRelatedId(booking.getBookingId());
            notification.setIsRead(false);

            notificationRepository.save(notification);
        } catch (Exception e) {
            log.warn("Failed to create success notification for booking: {}", booking.getBookingCode(), e);
        }
    }

    /**
     * Tạo notification backup khi email fail
     */
    // Tạo notification backup khi email fail
    @Override
    public void createEmailFailureNotification(BookingDto booking) {
        try {
            Notification notification = new Notification();
            notification.setUser(userMapper.toEntity(booking.getUser()));
            notification.setTitle("Đặt vé thành công - " + booking.getBookingCode());
            notification.setMessage(String.format(
                    "Chúc mừng! Bạn đã đặt vé thành công.\n" +
                            "Mã đặt vé: %s\n" +
                            "Thời gian thanh toán: %s\n" +
                            "Email xác nhận sẽ được gửi trong thời gian sớm nhất.",
                    booking.getBookingCode(),
                    booking.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            ));
            notification.setNotificationType(Notification.NotificationType.booking);
            notification.setRelatedId(booking.getBookingId());
            notification.setIsRead(false);

            notificationRepository.save(notification);
            log.info("Created backup notification for booking: {}", booking.getBookingCode());

        } catch (Exception e) {
            log.error("Failed to create backup notification for booking: {}", booking.getBookingCode(), e);
        }
    }

    @Override
    public void createPermanentFailureNotification(BookingDto booking) {
        try {
            Notification notification = new Notification();
            notification.setUser(userMapper.toEntity(booking.getUser()));
            notification.setTitle("Thông tin đặt vé - " + booking.getBookingCode());
            notification.setMessage(String.format(
                    "Đặt vé của bạn đã thành công!\n" +
                            "Mã đặt vé: %s\n" +
                            "Thời gian thanh toán: %s\n" +
                            "Vui lòng liên hệ bộ phận CSKH để nhận thông tin chi tiết về vé.",
                    booking.getBookingCode(),
                    booking.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            ));
            notification.setNotificationType(Notification.NotificationType.booking);
            notification.setRelatedId(booking.getBookingId());
            notification.setIsRead(false);

            notificationRepository.save(notification);

            // Optional: Notify admin
            notifyAdminEmailFailure(booking);

        } catch (Exception e) {
            log.error("Failed to create permanent failure notification", e);
        }
    }

    @Override
    public void notifyAdminEmailFailure(BookingDto booking) {
        try {
            log.error("ADMIN ALERT: Email confirmation failed permanently for booking: {}",
                    booking.getBookingCode());
            // Có thể gửi Slack, email admin, hoặc tạo admin notification
        } catch (Exception e) {
            log.error("Failed to notify admin about email failure", e);
        }
    }


    @Override
    public void sendEmailWithQRCode(String to, String subject, String text, byte[] qrCodeBytes) throws MessagingException {


        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);

        // Nội dung email dạng HTML có nhúng ảnh
        helper.setText(text, true);

        // Đính kèm ảnh QR code inline với Content-ID = qrCodeImage
        helper.addInline("qrCodeImage", new ByteArrayResource(qrCodeBytes), "image/png");

        mailSender.send(message);
    }

    @Async("emailExecutor")
    @Retryable(
            value = {MailException.class, MessagingException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    @Override
    public void sendTripStatusEmail(String to, String tripCode, LocalDateTime departureTime, Trip.Status status, Integer delayMinutes, String reason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);

            if (Trip.Status.delayed.equals(status)) {
                helper.setSubject("⏰ Thông báo trễ chuyến tàu " + tripCode);
                helper.setText(TemplateMail.buildTripDelayHtml(tripCode, departureTime, delayMinutes, reason), true);
            } else if (Trip.Status.cancelled.equals(status)) {
                helper.setSubject("🚫 Thông báo huỷ chuyến tàu " + tripCode);
                helper.setText(TemplateMail.buildTripCancelHtml(tripCode, departureTime, reason), true);
            } else {
                log.warn("⚠️ Không gửi email vì trạng thái không hợp lệ: {}", status);
                return;
            }

            mailSender.send(message);
            log.info("✅ Đã gửi email {} tới {}", status.equals(Trip.Status.delayed) ? "trễ chuyến" : "huỷ chuyến", to);

        } catch (MessagingException e) {
            log.error("❌ Lỗi gửi email {} tới {}", status, to, e);
            throw new MailSendException("Không gửi được email trạng thái chuyến tàu", e);
        }
    }

    @Recover
    public void recover(MailException e, String to, String tripCode, LocalDateTime departureTime, Trip.Status status, Integer delayMinutes, String reason) {
        if( status.equals(Trip.Status.cancelled) ) delayMinutes =null;
      //  log.error("❌ Email gửi thất bại vĩnh viễn tới {} cho chuyến {} trạng thái {}: {}", to, tripCode, status, e.getMessage());
        // TODO: Ghi log DB, tạo job retry sau hoặc báo admin
        FailedEmailLog log = FailedEmailLog.builder()
                .recipientEmail(to)
                .tripCode(tripCode)
                .departureTime(departureTime)
                .status(status)
                .delayMinutes(delayMinutes)
                .reason(reason)
                .errorMessage(e.getMessage())
                .retryCount(0)
                .resolved(false)
                .createdAt(LocalDateTime.now())
                .build();

        failedEmailLogRepository.save(log);
    }

    @Async("emailExecutor")
    @Retryable(
            value = {MailException.class, MessagingException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    @Override
    public void sendBookingCancelEmail(String to, Booking booking, String tripCode , String reason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("❌ Xác nhận huỷ vé: " + booking.getBookingCode());

            // Nội dung HTML (dùng template riêng nếu có)
            String content = TemplateMail.buildBookingCancelHtml(
                    booking.getBookingCode(),
                    tripCode,
                    booking.getBookingDate(),
                    reason
            );

            helper.setText(content, true);
            mailSender.send(message);
            log.info("✅ Đã gửi email huỷ vé tới {}", to);

        } catch (MessagingException e) {
            log.error("❌ Gửi email huỷ vé thất bại tới {}: {}", to, e.getMessage());
            throw new MailSendException("Gửi email huỷ vé thất bại", e);
        }
    }

    @Override
    public void sendRefundRequestedEmail(Booking booking, List<Ticket> tickets, double refundAmount, RefundPolicy policy) {

            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setTo(booking.getContactEmail());
                helper.setSubject("🔁 Yêu cầu hoàn vé - " + booking.getBookingCode());

                String htmlContent = TemplateMail.buildRefundRequestHtml(booking, tickets, refundAmount, policy);
                helper.setText(htmlContent, true);

                mailSender.send(message);
                log.info("✅ Đã gửi email xác nhận yêu cầu hoàn vé cho booking {}", booking.getBookingCode());

            } catch (MessagingException e) {
                log.error("❌ Gửi email hoàn vé thất bại: {}", e.getMessage());
                throw new MailSendException("Gửi email hoàn vé thất bại", e);
            }
    }
    @Override
    public void sendRefundApprovedEmail(Booking booking, List<TicketDto> tickets, double refundAmount) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getContactEmail());
            helper.setSubject("✅ Yêu cầu hoàn vé đã được duyệt - " + booking.getBookingCode());
            BookingDto bookingDto = bookingMapper.toDto(booking);

            String htmlContent = TemplateMail.buildRefundApprovedHtml(booking, tickets, refundAmount);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("✅ Đã gửi email duyệt hoàn vé cho booking {}", booking.getBookingCode());

        } catch (MessagingException e) {
            log.error("❌ Gửi email duyệt hoàn vé thất bại: {}", e.getMessage());
            throw new MailSendException("Gửi email duyệt hoàn vé thất bại", e);
        }
    }


    @Override
    public void sendRefundRejectedEmail(Booking booking, List<TicketDto> tickets, String reason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getContactEmail());
            helper.setSubject("❌ Yêu cầu hoàn vé bị từ chối - " + booking.getBookingCode());

            String htmlContent = TemplateMail.buildRefundRejectedHtml(booking, tickets, reason);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("✅ Đã gửi email từ chối hoàn vé cho booking {}", booking.getBookingCode());

        } catch (MessagingException e) {
            log.error("❌ Gửi email từ chối hoàn vé thất bại: {}", e.getMessage());
            throw new MailSendException("Gửi email từ chối hoàn vé thất bại", e);
        }
}}
