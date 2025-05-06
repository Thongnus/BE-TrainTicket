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
@Table(name = "ticket_prices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer priceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('hard_seat', 'soft_seat', 'hard_sleeper', 'soft_sleeper', 'vip')")
    private Carriage.CarriageType carriageType;

    @Column(nullable = false)
    private Double basePrice;

    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private Double weekendSurcharge;

    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private Double holidaySurcharge;

    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private Double peakHourSurcharge;

    @Column(columnDefinition = "DECIMAL(5,2) DEFAULT 0")
    private Double discountRate;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
