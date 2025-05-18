package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Promotion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Promotion}
 */
@Data
@NoArgsConstructor // ✅ BẮT BUỘC CHO JACKSON
@AllArgsConstructor
public class PromotionDto implements Serializable {
    Integer promotionId;
    String promotionCode;
    String promotionName;
    String description;
    Promotion.DiscountType discountType;
    Double discountValue;
    Double minimumPurchase;
    Double maximumDiscount;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Integer usageLimit;
    Integer usageCount;
    Promotion.Status status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}