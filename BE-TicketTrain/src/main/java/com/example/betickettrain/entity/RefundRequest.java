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
@Table(name = "refund_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_request_id")
    private Long refundRequestId;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "refund_policy_id", nullable = false)
    private RefundPolicy refundPolicy;

    @Column(name = "original_amount", nullable = false)
    private Double originalAmount;

    @Column(name = "discount_amount", nullable = false)
    private Double discountAmount;

    @Column(name = "net_amount", nullable = false)
    private Double netAmount;

    @Column(name = "refund_amount", nullable = false)
    private Double refundAmount;

    @Column(name = "refund_percentage", nullable = false)
    private Double refundPercentage;

    @Column(name = "hours_before_departure")
    private Long hoursBeforeDeparture;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('pending', 'approved', 'rejected', 'completed') DEFAULT 'pending'")
    private RefundStatus status;

    @Column(name = "admin_note")
    private String adminNote;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @CreationTimestamp
    @Column(name = "request_date", updatable = false)
    private LocalDateTime requestDate;

    @Column(name = "processed_date")
    private LocalDateTime processedDate;

    @Column(name = "processed_by")
    private String  processedBy; // Admin user who processed the request

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum RefundStatus {
            pending, approved, rejected
    }
}