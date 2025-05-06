package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Booking;
import com.example.betickettrain.entity.Payment;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Payment}
 */
@Value
public class PaymentDto implements Serializable {
    Integer paymentId;
    Double paymentAmount;
    LocalDateTime paymentDate;
    Booking.PaymentMethod paymentMethod;
    String transactionId;
    Payment.Status status;
    String paymentDetails;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}