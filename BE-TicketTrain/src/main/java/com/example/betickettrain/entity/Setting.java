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
@Table(name = "settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    private Integer settingId;

    @Column(name = "setting_key", nullable = false, unique = true, length = 100)
    private String settingKey;

    @Column(name = "setting_value", nullable = false, columnDefinition = "TEXT")
    private String settingValue;

    @Column(name = "setting_group", nullable = false, length = 50)
    private String settingGroup;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
