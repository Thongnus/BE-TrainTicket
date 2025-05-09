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
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "booking_code", nullable = false, unique = true, length = 20)
    private String bookingCode;

    @CreationTimestamp
    @Column(name = "booking_date", updatable = false)
    private LocalDateTime bookingDate;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, columnDefinition = "ENUM('pending', 'paid', 'refunded', 'cancelled') DEFAULT 'pending'")
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false, columnDefinition = "ENUM('pending', 'confirmed', 'cancelled', 'completed') DEFAULT 'pending'")
    private BookingStatus bookingStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, columnDefinition = "ENUM('credit_card', 'bank_transfer', 'e_wallet', 'cash')")
    private PaymentMethod paymentMethod;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum PaymentStatus {
        pending, paid, refunded, cancelled
    }

    public enum BookingStatus {
        pending, confirmed, cancelled, completed
    }

    public enum PaymentMethod {
        credit_card, bank_transfer, e_wallet, cash
    }
}
