package com.example.betickettrain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PopularRouteDTO {
    private Long id;
    private String name;
    private Long bookings;
    private Double revenue;

    public PopularRouteDTO(PopularRouteProjection  p) {
        this.id = p.getId();
        this.name = p.getName();
        this.bookings = p.getBookings();
        this.revenue = p.getRevenue();
    }
}