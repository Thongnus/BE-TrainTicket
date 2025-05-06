package com.example.betickettrain.dto;

import com.example.betickettrain.entity.TicketChange;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.TicketChange}
 */
@Value
public class TicketChangeDto implements Serializable {
    Integer changeId;
    TicketDto ticket;
    TripDto oldTrip;
    TripDto newTrip;
    SeatDto oldSeat;
    SeatDto newSeat;
    Double changeFee;
    LocalDateTime changeDate;
    String reason;
    TicketChange.Status status;
    UserDto processedBy;
    LocalDateTime processedDate;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}