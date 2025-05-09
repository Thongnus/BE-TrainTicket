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
@Table(
        name = "trip_schedules",
        uniqueConstraints = @UniqueConstraint(columnNames = {"trip_id", "station_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Integer scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(name = "scheduled_arrival")
    private LocalDateTime scheduledArrival;

    @Column(name = "scheduled_departure")
    private LocalDateTime scheduledDeparture;

    @Column(name = "actual_arrival")
    private LocalDateTime actualArrival;

    @Column(name = "actual_departure")
    private LocalDateTime actualDeparture;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            columnDefinition = "ENUM('scheduled', 'arrived', 'departed', 'cancelled', 'delayed') DEFAULT 'scheduled'"
    )
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Status {
        scheduled, arrived, departed, cancelled, delayed
    }
}
