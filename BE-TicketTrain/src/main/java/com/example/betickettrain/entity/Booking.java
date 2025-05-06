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
    private Integer bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 20)
    private String bookingCode;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime bookingDate;

    @Column(nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('pending', 'paid', 'refunded', 'cancelled') DEFAULT 'pending'")
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('pending', 'confirmed', 'cancelled', 'completed') DEFAULT 'pending'")
    private BookingStatus bookingStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('credit_card', 'bank_transfer', 'e_wallet', 'cash')")
    private PaymentMethod paymentMethod;

    private LocalDateTime paymentDate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
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
