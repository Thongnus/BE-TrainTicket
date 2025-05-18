package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Carriage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.TicketPrice}
 */
@Data
@NoArgsConstructor // ✅ BẮT BUỘC CHO JACKSON
@AllArgsConstructor
public class TicketPriceDto implements Serializable {
    Integer priceId;
    RouteDto route;
    Carriage.CarriageType carriageType;
    Double basePrice;
    Double weekendSurcharge;
    Double holidaySurcharge;
    Double peakHourSurcharge;
    Double discountRate;
    LocalDate startDate;
    LocalDate endDate;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}