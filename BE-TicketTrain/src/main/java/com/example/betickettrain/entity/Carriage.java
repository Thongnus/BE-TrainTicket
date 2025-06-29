package com.example.betickettrain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "carriages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carriage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carriage_id")
    private Integer carriageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @Column(name = "carriage_number", nullable = false, length = 10)
    private String carriageNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "carriage_type", nullable = false, columnDefinition = "ENUM('hard_seat', 'soft_seat', 'hard_sleeper', 'soft_sleeper', 'vip')")
    private CarriageType carriageType;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('active', 'maintenance', 'retired') DEFAULT 'active'")
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum CarriageType {
        hard_seat, soft_seat, hard_sleeper, soft_sleeper, vip
    }

    public enum Status {
        active, maintenance, retired
    }
}
