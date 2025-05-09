package com.example.betickettrain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Integer ticketId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_station_id", nullable = false)
    private Station originStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_station_id", nullable = false)
    private Station destinationStation;

    @Column(name = "passenger_name", nullable = false, length = 100)
    private String passengerName;

    @Column(name = "passenger_id_card", nullable = false, length = 20)
    private String passengerIdCard;

    @Column(name = "ticket_price", nullable = false)
    private Double ticketPrice;

    @Column(name = "ticket_code", nullable = false, unique = true, length = 20)
    private String ticketCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_status", nullable = false, columnDefinition = "ENUM('booked', 'checked_in', 'cancelled', 'used', 'expired') DEFAULT 'booked'")
    private Status status;

    @Column(name = "boarding_time")
    private LocalDateTime boardingTime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Status {
        booked, checked_in, cancelled, used, expired
    }
}
