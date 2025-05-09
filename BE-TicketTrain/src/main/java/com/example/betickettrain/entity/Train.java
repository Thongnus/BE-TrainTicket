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
@Table(name = "trains")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Train {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer trainId;

    @Column(nullable = false, unique = true, length = 20)
    private String trainNumber;

    @Column(length = 100)
    private String trainName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('express', 'local', 'fast', 'sleeper') NOT NULL")
    private TrainType trainType;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('active', 'maintenance', 'retired') DEFAULT 'active'")
    private Status status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum TrainType {
        express, local, fast, sleeper
    }

    public enum Status {
        active, maintenance, retired
    }
}
