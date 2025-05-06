package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Feedback;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Feedback}
 */
@Value
public class FeedbackDto implements Serializable {
    Integer feedbackId;
    Integer rating;
    String comment;
    LocalDateTime feedbackDate;
    Feedback.Status status;
}