package com.example.betickettrain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "refund_policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer policyId;

    @Column(nullable = false)
    private String policyName;

    @Column(nullable = false)
    private Integer hoursBeforeDeparture; // ví dụ: 24 = áp dụng nếu còn 24h trở lên

    @Column(nullable = false)
    private Double refundPercent; // ví dụ: 90.0 = hoàn 90%

    @Column
    private LocalDate applyFrom;

    @Column
    private LocalDate applyTo;

    @Column(columnDefinition = "TEXT")
    private String note;
}
