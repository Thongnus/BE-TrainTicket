package com.example.betickettrain.dto;

import lombok.Data;

import java.util.List;

@Data
public class BookingCheckoutRequest {
    private Integer userId;
    private Integer tripId;
    private String paymentMethod; // e.g. "VNPAY", "CASH"
    private List<PassengerTicketDto> passengerTickets;
    private String promotionCode;
}
