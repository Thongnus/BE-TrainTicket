package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.FeedbackDto;
import com.example.betickettrain.entity.Feedback;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface FeedbackMapper {
    Feedback toEntity(FeedbackDto feedbackDto);

    FeedbackDto toDto(Feedback feedback);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Feedback partialUpdate(FeedbackDto feedbackDto, @MappingTarget Feedback feedback);
}