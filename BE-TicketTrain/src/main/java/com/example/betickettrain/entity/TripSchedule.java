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
@Table(name = "trip_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    private LocalDateTime scheduledArrival;

    private LocalDateTime scheduledDeparture;

    private LocalDateTime actualArrival;

    private LocalDateTime actualDeparture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('scheduled', 'arrived', 'departed', 'cancelled', 'delayed') DEFAULT 'scheduled'")
    private Status status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum Status {
        scheduled, arrived, departed, cancelled, delayed
    }
}
