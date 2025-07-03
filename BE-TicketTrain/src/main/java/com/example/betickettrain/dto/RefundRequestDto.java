package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Booking;
import com.example.betickettrain.entity.Payment;
import com.example.betickettrain.entity.RefundRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.RefundRequest}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundRequestDto {
    private Long refundRequestId;

    // Thông tin từ Booking
    private Integer bookingId;
    private String bookingCode;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Booking.BookingStatus bookingStatus;


    // Thông tin từ Payment
    private Integer paymentId;
    private Double paymentAmount;
    private Booking.PaymentMethod paymentMethod;
    private Payment.Status paymentStatus;

    // Thông tin từ RefundPolicy
    private Long refundPolicyId;
    private String policyName;
    private Double policyRefundPercent;

    //thong tin trip
    private String tripCode;
    private String routeName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departureTime;
    private String originStationName;
    private String destinationStationName;


    // Thông tin RefundRequest
    private Double originalAmount;
    private Double discountAmount;
    private Double netAmount;
    private Double refundAmount;
    private Double refundPercentage;
    private Long hoursBeforeDeparture;
    private RefundRequest.RefundStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processedDate;
    private String adminNote;
    private String rejectionReason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}