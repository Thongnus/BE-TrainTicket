package com.example.betickettrain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Integer promotionId;

    @Column(name = "promotion_code", nullable = false, unique = true, length = 20)
    private String promotionCode;

    @Column(name = "promotion_name", nullable = false, length = 100)
    private String promotionName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, columnDefinition = "ENUM('percentage', 'fixed_amount')")
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false)
    private Double discountValue;

    @Column(name = "minimum_purchase", columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private Double minimumPurchase;

    @Column(name = "maximum_discount")
    private Double maximumDiscount;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "usage_count", columnDefinition = "INT DEFAULT 0")
    private Integer usageCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('active', 'inactive', 'expired') DEFAULT 'active'")
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum DiscountType {
        percentage, fixed_amount
    }

    public enum Status {
        active, inactive, expired
    }
}
