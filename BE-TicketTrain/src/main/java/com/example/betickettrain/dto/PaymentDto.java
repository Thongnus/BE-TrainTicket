package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Booking;
import com.example.betickettrain.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Payment}
 */
@Data
@NoArgsConstructor // ✅ BẮT BUỘC CHO JACKSON
@AllArgsConstructor
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
    public PaymentDto(String method, String status, String transactionId, LocalDateTime paidAt) {
        this.paymentMethod = Booking.PaymentMethod.valueOf(method);
        this.status = Payment.Status.valueOf(status);
        this.transactionId = transactionId;
        this.createdAt = paidAt;
    }
}