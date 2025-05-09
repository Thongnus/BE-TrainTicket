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
@Table(name = "stations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_id")
    private Integer stationId;

    @Column(name = "station_name", nullable = false, length = 100)
    private String stationName;

    @Column(name = "location", nullable = false, length = 255)
    private String location;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "province", nullable = false, length = 100)
    private String province;

    @Column(name = "phone", length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('active', 'maintenance', 'closed') DEFAULT 'active'")
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Status {
        active, maintenance, closed
    }
}
