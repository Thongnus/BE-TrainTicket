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
    @Column(name = "change_id") // Tên cột trong DB
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

    @Column(name = "change_fee", nullable = false) // Tên cột trong DB
    private Double changeFee;

    @CreationTimestamp
    @Column(name = "change_date", updatable = false) // Tên cột trong DB
    private LocalDateTime changeDate;

    @Column(name = "reason", columnDefinition = "TEXT") // Tên cột trong DB
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('pending', 'processed', 'cancelled') DEFAULT 'pending'") // Tên cột trong DB
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    private User processedBy;

    @Column(name = "processed_date") // Tên cột trong DB
    private LocalDateTime processedDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false) // Tên cột trong DB
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at") // Tên cột trong DB
    private LocalDateTime updatedAt;

    public enum Status {
        pending, processed, cancelled
    }
}

