package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Booking;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Booking}
 */
@Data
@NoArgsConstructor // ✅ BẮT BUỘC CHO JACKSON
@AllArgsConstructor
public class BookingDto implements Serializable {
    Integer bookingId;
    String bookingCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime bookingDate;
    Double totalAmount;
    Booking.PaymentStatus paymentStatus;
    Booking.BookingStatus bookingStatus;
    Booking.PaymentMethod paymentMethod;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime paymentDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
    private UserDto user;
    String contactEmail;
    String contactPhone;
    TripDto tripDto;
    Integer ticketCount;
}