package com.example.betickettrain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "failed_email_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FailedEmailLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipientEmail;
    private String tripCode;

    @Enumerated(EnumType.STRING)
    private Trip.Status status;

    private LocalDateTime departureTime;
    private Integer delayMinutes;

    private String reason;


    private String errorMessage;

    private Integer retryCount = 0;
    private LocalDateTime lastAttemptAt;
    private Boolean resolved = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}
