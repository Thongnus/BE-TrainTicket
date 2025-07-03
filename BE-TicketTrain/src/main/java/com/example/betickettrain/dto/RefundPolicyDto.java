package com.example.betickettrain.dto;

import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.example.betickettrain.entity.RefundPolicy}
 */
@Data
public class RefundPolicyDto implements Serializable {
    Integer policyId;
    String policyName;
    Integer hoursBeforeDeparture;
    Double refundPercent;
    LocalDate applyFrom;
    LocalDate applyTo;
    String note;
}