package com.example.betickettrain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.betickettrain.entity.Newfeed}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewfeedDto implements Serializable {
    Long id;
    String title;
    String description;
    String image;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String content;
}