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
@Table(name = "ticket_changes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketChange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer changeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_trip_id")
    private Trip oldTrip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_trip_id", nullable = false)
    private Trip newTrip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_seat_id")
    private Seat oldSeat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_seat_id", nullable = false)
    private Seat newSeat;

    @Column(nullable = false)
    private Double changeFee;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime changeDate;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('pending', 'processed', 'cancelled') DEFAULT 'pending'")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    private User processedBy;

    private LocalDateTime processedDate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum Status {
        pending, processed, cancelled
    }
}
