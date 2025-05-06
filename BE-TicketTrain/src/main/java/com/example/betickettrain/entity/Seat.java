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
@Table(name = "seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carriage_id", nullable = false)
    private Carriage carriage;

    @Column(nullable = false, length = 10)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('window', 'aisle', 'middle', 'lower_berth', 'middle_berth', 'upper_berth')")
    private SeatType seatType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('active', 'maintenance', 'unavailable') DEFAULT 'active'")
    private Status status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum SeatType {
        window, aisle, middle, lower_berth, middle_berth, upper_berth
    }

    public enum Status {
        active, maintenance, unavailable
    }
}
