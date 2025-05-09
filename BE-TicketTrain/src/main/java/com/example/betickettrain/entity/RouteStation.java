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
    @Column(name = "route_station_id")
    private Integer routeStationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(name = "stop_order", nullable = false)
    private Integer stopOrder;

    @Column(name = "arrival_offset", nullable = false)
    private Integer arrivalOffset;

    @Column(name = "departure_offset", nullable = false)
    private Integer departureOffset;

    @Column(name = "distance_from_origin", nullable = false)
    private Float distanceFromOrigin;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
