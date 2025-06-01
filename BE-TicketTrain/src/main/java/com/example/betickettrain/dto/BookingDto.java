package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Booking;
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
    LocalDateTime bookingDate;
    Double totalAmount;
    Booking.PaymentStatus paymentStatus;
    Booking.BookingStatus bookingStatus;
    Booking.PaymentMethod paymentMethod;
    LocalDateTime paymentDate;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    private UserDto user;


}