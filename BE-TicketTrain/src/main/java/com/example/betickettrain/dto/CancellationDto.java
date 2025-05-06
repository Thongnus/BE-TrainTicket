package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Cancellation;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Cancellation}
 */
@Value
public class CancellationDto implements Serializable {
    Integer cancellationId;
    LocalDateTime cancellationDate;
    Double refundAmount;
    Double cancellationFee;
    String reason;
    Cancellation.Status status;
    LocalDateTime processedDate;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}