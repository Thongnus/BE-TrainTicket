package com.example.betickettrain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class BookingHistoryDTO {
    private Integer bookingId;
    private String bookingCode;
    private String bookingStatus;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private Double totalAmount;
    private TripDto trip;
    private List<PassengerTicketDto> passengers;
    private PaymentDto payment;

}