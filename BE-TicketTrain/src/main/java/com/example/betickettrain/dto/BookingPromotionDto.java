package com.example.betickettrain.dto;

import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.BookingPromotion}
 */
@Value
public class BookingPromotionDto implements Serializable {
    Integer bookingPromotionId;
    Double discountAmount;
    LocalDateTime appliedAt;
}