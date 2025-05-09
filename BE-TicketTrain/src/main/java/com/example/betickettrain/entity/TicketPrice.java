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
    @Column(name = "price_id") // Chỉ định tên cột rõ ràng
    private Integer priceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Enumerated(EnumType.STRING)
    @Column(name = "carriage_type", nullable = false, columnDefinition = "ENUM('hard_seat', 'soft_seat', 'hard_sleeper', 'soft_sleeper', 'vip')")
    private Carriage.CarriageType carriageType;

    @Column(name = "base_price", nullable = false)
    private Double basePrice;

    @Column(name = "weekend_surcharge", columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private Double weekendSurcharge;

    @Column(name = "holiday_surcharge", columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private Double holidaySurcharge;

    @Column(name = "peak_hour_surcharge", columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private Double peakHourSurcharge;

    @Column(name = "discount_rate", columnDefinition = "DECIMAL(5,2) DEFAULT 0")
    private Double discountRate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
