package com.example.betickettrain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PassengerTicketDto {
    private Integer seatId;
    private String passengerName;
    private String identityCard;
}
