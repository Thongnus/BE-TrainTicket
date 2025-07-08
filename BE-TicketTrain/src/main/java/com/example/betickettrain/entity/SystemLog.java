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
@Table(name = "system_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id") // Tên cột trong DB
    private Integer logId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id") // Tên cột trong DB
    private User user;

    @Column(name = "action", nullable = false, length = 255) // Tên cột trong DB
    private String action;

    @Column(name = "entity_type", nullable = false, length = 50) // Tên cột trong DB
    private String entityType;

    @Column(name = "entity_id") // Tên cột trong DB
    private Integer entityId;

    @Column(name = "description", columnDefinition = "TEXT") // Tên cột trong DB
    private String description;

    @Column(name = "ip_address", length = 45) // Tên cột trong DB
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT") // Tên cột trong DB
    private String userAgent;

    @CreationTimestamp
    @Column(name = "log_time", updatable = false) // Tên cột trong DB
    private LocalDateTime logTime;
}

