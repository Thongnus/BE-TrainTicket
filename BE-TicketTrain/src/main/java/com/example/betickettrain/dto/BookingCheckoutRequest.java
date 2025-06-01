package com.example.betickettrain.dto;

import lombok.Data;

import java.util.List;

@Data
public class BookingCheckoutRequest {

    private Integer tripId;
    private String paymentMethod; // e.g. "VNPAY", "CASH"
    private String infoPhone;
    private String infoEmail;
    private List<PassengerTicketDto> passengerTickets;
    private String promotionCode;
    // Optional fields for return trip
    private Integer returnTripId;
    private List<PassengerTicketDto> returnPassengerTickets;
}
