package com.example.betickettrain.dto;

import com.example.betickettrain.entity.TicketChange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.TicketChange}
 */
@Data
@NoArgsConstructor // ✅ BẮT BUỘC CHO JACKSON
@AllArgsConstructor
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