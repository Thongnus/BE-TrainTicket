package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;

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
}
