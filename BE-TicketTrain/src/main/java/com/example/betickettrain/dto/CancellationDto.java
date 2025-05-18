package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Cancellation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Cancellation}
 */
@Data
@NoArgsConstructor // ✅ BẮT BUỘC CHO JACKSON
@AllArgsConstructor
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