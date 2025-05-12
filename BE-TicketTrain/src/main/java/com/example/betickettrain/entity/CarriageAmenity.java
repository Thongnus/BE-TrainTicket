package com.example.betickettrain.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "carriage_amenities", uniqueConstraints = {
        @UniqueConstraint(columnNames = "carriage_id", name = "unique_carriage_amenity")
})
public class CarriageAmenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carriage_amenity_id")
    private Integer carriageAmenityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carriage_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_carriage_amenities_carriage_id",
                    foreignKeyDefinition = "FOREIGN KEY (carriage_id) REFERENCES carriages(carriage_id) ON DELETE CASCADE"))
    private Carriage carriage;

    @Column(name = "wifi", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean wifi = false;

    @Column(name = "power_plug", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean powerPlug = false;

    @Column(name = "food", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean food = false;

    @Column(name = "tv", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean tv = false;

    @Column(name = "massage_chair", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean massageChair = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    // Constructor mặc định
    public CarriageAmenity() {
    }
}