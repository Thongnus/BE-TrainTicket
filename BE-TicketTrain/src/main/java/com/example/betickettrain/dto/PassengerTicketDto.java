package com.example.betickettrain.dto;

import lombok.Data;

@Data
public class PassengerTicketDto {
    private Integer seatId;
    private String passengerName;
    private String identityCard;
}
