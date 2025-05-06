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
@Table(name = "route_stations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer routeStationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(nullable = false)
    private Integer stopOrder;

    @Column(nullable = false)
    private Integer arrivalOffset;

    @Column(nullable = false)
    private Integer departureOffset;

    @Column(nullable = false)
    private Float distanceFromOrigin;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
