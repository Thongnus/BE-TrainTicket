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
    private Integer promotionId;

    @Column(nullable = false, unique = true, length = 20)
    private String promotionCode;

    @Column(nullable = false, length = 100)
    private String promotionName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('percentage', 'fixed_amount')")
    private DiscountType discountType;

    @Column(nullable = false)
    private Double discountValue;

    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private Double minimumPurchase;

    private Double maximumDiscount;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    private Integer usageLimit;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer usageCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('active', 'inactive', 'expired') DEFAULT 'active'")
    private Status status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum DiscountType {
        percentage, fixed_amount
    }

    public enum Status {
        active, inactive, expired
    }
}
