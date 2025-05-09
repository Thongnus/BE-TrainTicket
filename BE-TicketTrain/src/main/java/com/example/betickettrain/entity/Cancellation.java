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
@Table(name = "cancellations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cancellation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cancellation_id")
    private Integer cancellationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @CreationTimestamp
    @Column(name = "cancellation_date", updatable = false)
    private LocalDateTime cancellationDate;

    @Column(name = "refund_amount")
    private Double refundAmount;

    @Column(name = "cancellation_fee")
    private Double cancellationFee;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('pending', 'approved', 'rejected', 'refunded') DEFAULT 'pending'")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    private User processedBy;

    @Column(name = "processed_date")
    private LocalDateTime processedDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Status {
        pending, approved, rejected, refunded
    }
}
