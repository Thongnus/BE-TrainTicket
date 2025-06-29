package com.example.betickettrain.schedule;

import com.example.betickettrain.entity.FailedEmailLog;
import com.example.betickettrain.repository.FailedEmailLogRepository;
import com.example.betickettrain.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EmailRetryJob {

    private final FailedEmailLogRepository failedEmailLogRepository;
    private final EmailService emailService;

    @Scheduled(fixedDelay = 600000) // Mỗi 10 phút
    public void retryFailedEmails() {
        List<FailedEmailLog> failedLogs = failedEmailLogRepository.findByResolvedFalseAndRetryCountLessThan(3);

        for (FailedEmailLog log : failedLogs) {
            try {
                emailService.sendTripStatusEmail(
                        log.getRecipientEmail(),
                        log.getTripCode(),
                        log.getDepartureTime(),
                        log.getStatus(),
                        log.getDelayMinutes(),
                        log.getReason()
                );

                log.setResolved(true);
                log.setLastAttemptAt(LocalDateTime.now());
                failedEmailLogRepository.save(log);

            } catch (MailException e) {
                log.setRetryCount(log.getRetryCount() + 1);
                log.setLastAttemptAt(LocalDateTime.now());
                log.setErrorMessage(e.getMessage());
                failedEmailLogRepository.save(log);
                // Không throw lại để job vẫn tiếp tục gửi các log còn lại
            }
        }
    }
}
