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
@Table(name = "booking_promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingPromotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingPromotionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @Column(nullable = false)
    private Double discountAmount;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime appliedAt;
}
