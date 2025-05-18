package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Ticket}
 */
@Data
@NoArgsConstructor // ✅ BẮT BUỘC CHO JACKSON
@AllArgsConstructor
public class TicketDto implements Serializable {
    Integer ticketId;
    BookingDto booking;
    TripDto trip;
    SeatDto seat;
    StationDto originStation;
    StationDto destinationStation;
    String passengerName;
    String passengerIdCard;
    Double ticketPrice;
    String ticketCode;
    Ticket.Status status;
    LocalDateTime boardingTime;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}