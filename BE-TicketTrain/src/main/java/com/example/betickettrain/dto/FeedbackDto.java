package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Feedback;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Feedback}
 */
@Data
@NoArgsConstructor // ✅ BẮT BUỘC CHO JACKSON
@AllArgsConstructor
public class FeedbackDto implements Serializable {
    Integer feedbackId;
    Integer rating;
    String comment;
    LocalDateTime feedbackDate;
    Feedback.Status status;
}