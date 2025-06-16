package com.example.betickettrain.dto;

public interface  PopularRouteProjection {
     Long getId();
     String getName();
     Long getBookings(); // ⚠️ sửa từ Integer → Long
     Double getRevenue();
}
